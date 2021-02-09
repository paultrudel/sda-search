# Searchable Document Archive (SDA-Search)

The purpose of this project was to obtain a more in-depth understanding of the aspects of web-based information retrieval. This was accomplished through the design of a small scale search engine. The design of a search engine is a multi-faceted project requiring four primary steps:

1. Crawling web pages
2. Parsing the crawled content
3. Indexing the content
4. Retrieving the content

Each step in this process has its own complexities and in particular the retrieval portion has many approaches to accomplish the task in a manner that user friendly; taking into account both speed of retrieval and the relevancy of returned content.


## Crawling the Web

To facilitate web crawling the Java library Crawler4j was used. This library is simple to used and provides all of the tools necessary for basic web crawling. Three seed domains were used as starting points for thge crawling and a maximum of 10,000 web pages were visited and parsed.


## Parsing Web Content

To parse the web pages that were visited the Java library JSoup was used. The library allows the used to select elements from the web content by html tag, such as paragraph <p> or image <img> tags. Once the elements is selected the used can then extract the information that commonly contained in such tags. Such as text for a simple paragraph tag, or the image source and alternate attributes for an image tag. Once parsed these web pages are then stored as a project specific document, 'SDADocument', in the database.

### Summarizing Text Content

The are two main classes of text summarization techniques; extractive and abstractive. Extractive summarization techniques work by stitching together portions of the source text verbatim, the vast majority of summarization done in practice is extractive. Abstractive methods instead seek to rephrase the source text in the same manner as how humans create summaries. Abstractive summarization is still in its infancy since related NLP problems such as semantic reprsentation and natural language generation are still being worked on.

The summarization algorithm used in this project is an extractive algorithm and functions following these steps:
1. Split the source text into paragraphs
2. Split each paragraph into sentences
3. Create an N x N intersection matrix, where N is the number of sentences in the text. Each entry (i, j) in the matrix is the intersection value of sentence i with sentence j, the intersection value is the number of words the sentences have in common divided by the average number of words in the two sentences
4. Map each sentence to it's total intersection value, the total intersection value for a sentence is the row sum corresponding to that sentence
5. From each paragraph extract the M sentences with highest intersection scores, where M is the number of sentences in the paragraph divided by 5
6. Sort the sentences collected from each paragraph by the order that they appear in the source text

The primary assumption made by this algorithm is that any sentence that has a high degree of intersection with all other sentences most likely contains a lot of information. Stitching together a collection of these high information sentences should then lead to an adequate summarization of the source content. Possible improvements to this algorithm could be made by removing stop words from consideration in the computation of sentence to sentence intersection values.


## Indexing the Content

Apache Lucene was used for the indexing and search purposes of this application.

### How Lucene Stores Information

Terminology:
- A SDADocument is a project specific database entity which models a web page
- A document refers to a Lucene specific term for a unit of indexing/search which is made up of a group of information fields  

For this project approximately 10,000 SDADocuments make up the document corpus. Each SDADocument contains a body of text and job of the index is to store information about this text content in a manner that allows for fast lookup of which documents contain a particular term. To do this Lucene stores this term-document information in an inverted index which maps each distinct term in the corpus to a list of documents, unqiuely identified by a serial number. Each entry in this associated list of documents is tuple containing the document's unqiue identified and the position in the document where the term is found. 
The advatage of storing information this way is that the memory needed for storage is very small compared to the actual size of a document's content and grows slowly as more documents are added to the index. The lookup time is also quite fast since it has a time complexity of approximately O(log n), where n is the number of distinct terms in the corpus. The process of building the document index also involves other common NLP techniques such as the dropping of stop words, stemming, and lemmatization.


## Retrieving the Content

The process of querying the index to search for relevant content is the most complex aspect of a search engine and the one that has the most variey in the way it can be approached. Large scale seach engines such as Google use hundreds of factors in their search process to determine what content is relevant to a user's query. The search engine in this project uses two factors, TF-IDF and PageRank scores.

### Boolean Model

One of the most basic ways of identifying potentially relevant documents from the index is to use boolean queries to check for the prescence or abscence of query terms in a document. The use of keywords such as "and", "or", and "not" are used to join terms in a query. The problem with this model is that it either returns too few results if the terms are joined by "and", or too many results if terms are joined by "or". Another issue is that a boolean query simply returns a set of documents matching the query, these documents have no ordering in terms of how relevant one document is to another.

### Ranked Retrieval

Instead of returning a set of documents satisfying a query return an ordering of the top documents from the collection for the query. This deals with the problem of too many results since only the top 10 or 100 documents for instance will be returned. This introduces the problem of how to score documents with term frequency and weighting being the most fundamental method of doing so.
The simplest way to score a document is to compute the score of as the frequency, or the number of times, the query term in the document. The problem is that document relevancy does not increase proportionally with term frequency. Another issue is that not all terms are equal, some terms have little or no impact in determining relevance. The solution is to measure the term frequency using the inverse document frequency and compute the tf-idf weight for each term-document pair.

![IR_Equations]

The tf-idf weight is the best known weighting scheme for information retrieval.

### Vector Space Model

Each document in the corpus can be viewed as an N-dimensional vector, where N is the number of distinct terms, with each entry in the vector being a tf-idf weight. Any term that does not appear in the document will have a weight of zero in the corresponding vector entry. To improve the speed of computations, in practice only terms appearing in the user's query are considered. Therefore, each document is viewed as a Q-dimensional vector, where Q is the number of distinct terms in the query.

### Scoring Documents

The set of documents are seen as a set of vectors in a vector space and the user query itself is represented a vector in this space. Documents are ranked in terms of their angle between the document and query vectors. Documents can either be ranked in decreasing order of their angle or increasing order of the cosine of their angle. The angle between a document and the query will vary between 0 and 90 degrees, with 0 degrees, cos(0) = 1, indicating a perfect match between a document and the query, and 90 degrees, cos(90) = 0, indicating that the vectors are orthogonal and therefore have no similarity.

![Cosine_Similarity]

Lucene makes use of both the boolean and vector space models. The boolean model is used to identify potentially relevant documents from the corpus, these documents are then ranked using the vector space model with the cosine similarity between a document and the query being used as the document's score.

### Boosting Document Scores and the PageRank Algorithm

Most search engines that are tasked with returning text based content typically make use of tf-idf weights as the first factor in determining document relevancy. This is a good starting point but ranking documents based solely the content of a page leads to the problem of spam. Creators of web content will seek to include an abundance of "valuable" terms on their pages in order to manipulate the scoring algorithm into giving a high score to their page even if it is not particularly relevant to a user's query. The most famous solution to this problem is the PageRank algorithm which was used in the first generation of the Google search engine. 
The PageRank algorithm solves this problem by modelling a user randomly navigating through a set of webpages as a stochastic process, in particular a Markov chain. Every web page is a state in the chain with each out-link on a page being a transition to another state. To solve the issue of a page having no out-links the "teleport" operation is introduced. This opertion is the equivalent of a user typing a URL into the browser allowing a user to access any page from any other page. The PageRank score of a web page is the probability that a user randomly browsing will end up on that page. A page that is linked to by few or no other pages will have a low probability of being viewed and in turn have a low PageRank score. The PageRank score is used to modify, or "boost", the score given to a page by Lucene. The PageRank score is used as a multiplier meaning that pages with a near zero probability of being randomly viewed will end up with a score that is near zero and therefore be poorly ranked. This approach is highly effective at filtering out spam web pages and other irrelevant web content.

### PageRank In-depth

The PageRank algorithm relies on the ability to determine the probability of a user visting a specific web page in a set of pages. To do this the algorithm first models the user as randomly moving through a discrete-time Markov chain (DTMC) with each state in the chain representing a web page. Outlinks on a page represent transitions from that page to other pages in the chain. The assumption is made that the user will click any of these links with equal probability. The "teleport" operation is introduced for two reasons, the first being to solve the problem of pages with no outlinks. This operation allows a user to move to any state from any other state. The next assumption made is that a user will use this operation on any page with probability α, with α usually begin 0.1, and follow an outlink with probability 1-α.

To summarize:
- A user will either follow an outlink with probability (1-α) or use teleport with probability α, assume α is 0.1
  - If the user follows an outlink they will follow any outlink with probability 1/N, where N is the number of outlinks on the page
  - If the user uses teleport they will go to any other web page with probability 1/M, where M is the number of pages in the chain
- If there are no outlinks on the current page the user will use teleport with probability 1

From this point on M represents the number of pages in the chain and N represents the number of outlinks on a given page.

With this model in place the probability of a user visiting any specific web page must be determined. The limiting distribution of the chain provides these probilities. To obtain this distribution the model must be represented using a transition probability matrix. A transition probability matrix, P, is an M x M matrix with each entry (i, j) being the probability of transitioning to state j from state i.

![Limiting_Distribution_Definition]

The limiting distribution of a Markov chain only exists under certain conditions, namely the chain must be both irreducible and aperiodic. A chain with both these properties is commonly referred to as ergodic. An irreducible chain is one where every state can be reached from any other state. An aperiodic chain is a chain that has a period of one. The period of a chain is the greatest common divisor of the periods of each state in the chain. The period of a state is the shortest number of transitions required to return to the state after leaving it. A state that can transition to itself therefore has a period of one. If a chain is irreducible and any state in the chain has a period of one then the greatest common divisor of the periods of all the states is one and therefore the chain has a period of one and is hence aperiodic. So, the easiest way to get an aperiodic chain is to have at least one self-loop, i.e. a state that can transition to itself. This is the second reason for the "teleport" operation. The "teleport" operation allows a user to go from any state to any other state, including the state they were just in. The "teleport" operation guarantees that the chain is both irreducible and aperiodic and therefore guarantees that the limiting distribution will exist.

Expanding on the definition of the limiting distribution we obtain the stationarity equations of the chain and ultimately the definition of the stationary distribution of the chain. The stationary distribution exists if and only if the limiting distribution exists and the chain has a finite number of states. If all of the above conditions are true then the limiting distribution is also a stationary distribution. The stationary distribution of a Markov chain is unique.

![Stationary_Distribution_Definition]

Since the Markov chain used for the PageRank model has a finite number of states the limiting distribution of the chain is also stationary. This provides and additional way, through the use of the chain's stationary equations, to compute the desired probabilities.

The definition of the limiting distribution provides the main way of computing the limiting distribution of a Markov chain. Repeated squaring of the probability matrix will eventually lead to the matrix converging to the limiting distribution. In practice the matrix normally only needs to be squared 50 to 100 times before it converges satisfactorily.

Since the limiting distribution is also stationary an alternate method is to find the limiting/stationary distribution using the stationarity equations. Solving the system of linear equations will yield the limiting distribution.

![Stationarity_Equations]

The final main method of obtaining the limiting distribution is to use Perron-Frobenius theorem and the not so obvious fact that the limiting distribution vector is an eigenvector of the transition probability matrx. The theorem states that any real-valued square matrix with all positive entries will have a unique largest real eigenvalue and the corresponding eigenvector can be chosen to have all positive entries. For transition matrices corresponding to an ergodic Markov chain this eigenvalue is 1 and the corresponding eigenvector is a probability vector that contains the limiting distribution.

### PageRank in Practice

The first step in the implementation of the PageRank algorithm is the creation of a crawl graph. During web crawling a graph is maintained which adds each of the pages visited by the crawler as a vertex in the graph. An edge is added between the newly visited page and the parent page. Once crawling is complete the graph can be transformed into an adjacency matrix. The next step is to produce a transition probability matrix from this adjacency matrix. 

Creating a transition matrix from the adjacency matrix can be done by using the following rules:
1. If a row of the adjacency matrix has no 1's then replace each element in the row by 1/M
2. For all other rows do the following
    1. Divide each 1 by the number of 1's in the row
    2. Multiply the resulting matrix by (1-α)
    3. Add α/M to every entry in the matrix

Once the transition probability matrix has been created the limiting distribution of the Markov chain represented by the matrix can be computed. This computing is normally done through repeated squaring of the transition matrix. The values from the distribution are then used to boost the score for their corresponding document.



[IR_Equations]: https://latex.codecogs.com/gif.latex?idf_t%20%3D%20log%28N/df_t%29%20%5C%5C%20%5Cindent%20w_%7Bt%2Cd%7D%20%3D%20log%281%20&plus;%20tf_%7Bt%2Cd%7D%29%20%5Ctimes%20log%28N/df_t%29%20%5C%5C%20%5C%5C%20%5Cindent%20N%20%3D%20the%5C%2Cnumber%5C%2Cof%5C%2Cdocuments%5C%2Cin%5C%2Cthe%5C%2Ccorpus%20%5C%5C%20%5Cindent%20df_t%20%3D%20number%5C%2Cof%20%5C%2Cdocuments%5C%2Cwhich%5C%2Ccontain%5C%2Cterm%5C%3At%20%5C%5C%20%5Cindent%20tf_%7Bt%2Cd%7D%20%3D%20frequency%5C%2Cof%5C%2Cterm%5C%3At%5C%3Ain%5C%2Cdocument%5C%3Ad%20%5C%5C%20%5Cindent%20idf_t%20%3D%20inverse%5C%2Cdocument%5C%2Cfrequence%5C%2Cof%5C%2Cterm%5C%3At%20%5C%5C%20%5Cindent%20w_%7Bt%2Cd%7D%20%3D%20tf%5C%21%5C%21-%5C%21%5C%21idf%5C%3Aweight%5C%2Cof%5C%2Ca%5C%2Cterm

[Cosine_Similarity]: https://latex.codecogs.com/gif.latex?cos%28%5Cvec%7Bq%7D%2C%5Cvec%7Bd%7D%29%3D%5Cfrac%7B%5Cvec%7Bq%7D%20%5Cbullet%20%5Cvec%7Bd%7D%7D%7B%7C%7C%5Cvec%7Bq%7D%7C%7C%5C%3A%7C%7C%5Cvec%7Bd%7D%7C%7C%7D%3D%5Cfrac%7B%5Csum_%7Bi%3D1%7D%5E%7B%7Cq%7C%7D%20q_i%5C%2Cd_i%7D%7B%5Csqrt%7B%5Csum_%7Bi%3D1%7D%5E%7B%7Cq%7C%7Dq_i%5E2%7D%5Csqrt%7B%5Csum_%7Bi%3D1%7D%5E%7B%7Cq%7C%7Dd_i%5E2%7D%7D%20%5C%5C%20%5Cvec%7Bq%7D%3Dquery%5C%2Cvector%20%5C%5C%20%5Cvec%7Bd%7D%3Ddocument%5C%2Cvector

[Limiting_Distribution_Definition]: https://latex.codecogs.com/gif.latex?P%3Dtransition%5C%2Cprobability%5C%2Cmatrix%20%5C%5C%20%5Cindent%20P%5En%3Dn%5C%21-%5C%21step%5C%2Ctransition%5C%2Cprobability%5C%2Cmatrix%20%5C%5C%20%5Cindent%20Let%5C%2C%5Cpi_j%3D%5Clim_%7Bn%5Cto%5Cinfty%7DP_%7Bij%7D%5En%20%5C%5C%20%5Cindent%20%5Cpi_j%5C%2Cis%5C%2Cthe%5C%2Climiting%5C%2Cprobability%5C%2Cof%5C%2Cthe%5C%2Cchain%5C%2Cbeing%5C%2Cin%5C%2Cstate%5C%2Cj%5C%2Cindependent%5C%2Cof%5C%2Cthe%5C%2Cstarting%5C%2Cstate%5C%2Ci%20%5C%5C%20%5Cindent%20%5Cvec%7B%5Cpi%7D%3D%28%5Cpi_0%2C%5Cpi_1%2C...%2C%5Cpi_%7BM-1%7D%29%2C%20%5Csum_%7Bi%3D1%7D%5E%7BM-1%7D%20%5Cpi_i%3D1%20%5C%5C%20%5Cindent%20%5Cvec%7B%5Cpi%7D%5C%2Cis%5C%2Cthe%5C%2Climiting%5C%2Cdistribution%5C%2Cof%5C%2Cthe%5C%2Cchain

[Stationary_Distribution_Definition]: https://latex.codecogs.com/gif.latex?%5Cpi_j%3D%5Clim_%7Bn%5Cto%5Cinfty%7DP_%7Bij%7D%5E%7Bn&plus;1%7D%3D%5Clim_%7Bn%5Cto%5Cinfty%7D%5Csum_%7Bk%3D0%7D%5E%7BM-1%7DP_%7Bik%7D%5En%5Ccdot%7BP_%7Bkj%7D%7D%3D%20%5Csum_%7Bk%3D0%7D%5E%7BM-1%7D%5Clim_%7Bn%5Cto%5Cinfty%7DP_%7Bik%7D%5En%5Ccdot%7BP_%7Bkj%7D%7D%3D%5Csum_%7Bk%3D0%7D%5E%7BM-1%7D%5Cpi_kP_%7Bkj%7D%20%5C%5C%20%5Cindent%20%5Cpi_j%3D%5Csum_%7Bi%3D0%7D%5E%7BM-1%7D%5Cpi_iP_%7Bij%7D%5Cquad%20are%5C%3Athe%5C%3Astationarity%5C%3Aequations%5C%3Aof%5C%3Athe%5C%3Achain%20%5C%5C%20%5Cindent%20A%5C%3Aprobability%5C%3Adistribution%5C%3Afor%5C%3Athe%5C%3AMarkov%5C%3Achain%5C%3Ais%5C%3Astationary%5C%3Aif%20%5C%5C%20%5Cindent%20%5Cvec%7B%5Cpi%7D%5Ccdot%7BP%7D%3D%5Cvec%7B%5Cpi%7D%5Cquad%20and%20%5Cquad%20%5Csum_%7Bi%3D0%7D%5E%7BM-1%7D%5Cpi_i%3D1

[Stationarity_Equations]: https://latex.codecogs.com/gif.latex?%5Cvec%7B%5Cpi%7D%20P%3D%5Cvec%7B%5Cpi%7D%20%5C%5C%20%5Cindent%20%28%5Cpi_1%5C%3A%5Cpi_2%5C%3A...%5C%3A%5Cpi_%7BM%7D%29P%3D%28%5Cpi_1%5C%3A%5Cpi_2%5C%3A...%5C%3A%5Cpi_%7BM%7D%29%20%5C%5C%20%5Cindent%20%5Cpi_1%3D%5Csum_%7Bj%3D1%7D%5E%7BM%7D%5Cpi_%7B1j%7D%3D%5Cpi_%7B1%2C1%7D&plus;%5Cpi_%7B1%2C2%7D&plus;...&plus;%5Cpi_%7B1%2CM%7D%20%5C%5C%20%5Cindent%20%5Cpi_2%3D%5Csum_%7Bj%3D1%7D%5E%7BM%7D%5Cpi_%7B2j%7D%3D%3D%5Cpi_%7B2%2C1%7D&plus;%5Cpi_%7B2%2C2%7D&plus;...&plus;%5Cpi_%7B2%2CM%7D%20%5C%5C%20%5Cindent%20.%20%5C%5C%20%5Cindent%20.%20%5C%5C%20%5Cindent%20.%20%5C%5C%20%5Cindent%20%5Cpi_%7BM-1%7D%3D%5Csum_%7Bj%3D1%7D%5E%7BM%7D%5Cpi_%7B%28M-1%29j%7D%3D%5Cpi_%7B%28M-1%29%2C1%7D&plus;%5Cpi_%7B%28M-1%29%2C2%7D&plus;...&plus;%5Cpi_%7B%28M-1%29%2CM%7D%20%5C%5C%20%5Cindent%201%3D%5Csum_%7Bi%3D1%7D%5EM%5Cpi_i%3D%5Cpi_1&plus;%5Cpi_2&plus;...&plus;%5Cpi_M
