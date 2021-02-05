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

Lucene makes use of both the boolean and vector space models. The boolean model is used to identify potentially relevant documents from the corpus, these documents are then ranked using the vector space model with the cosine similarity between a document and the query being used as the documents score.


### Boosting Document Scores and the PageRank Algorithm

Most search engines that are tasked with returning text based content typically make use of tf-idf weights as the first factor in determining document relevancy. This is a good starting point but ranking documents based solely the content of a page leads to the problem of spam. Creators of web content will seek to include an abundance of "valuable" terms on their pages in order to manipulate the scoring algorithm into giving a high score to their page even if it is not particularly relevant to a user's query. The most famous solution to this problem is the PageRank algorithm which was used in the first generation of the Google search engine.



[IR_Equations]: https://latex.codecogs.com/gif.latex?idf_t%20%3D%20log%28N/df_t%29%20%5C%5C%20%5Cindent%20w_%7Bt%2Cd%7D%20%3D%20log%281%20&plus;%20tf_%7Bt%2Cd%7D%29%20%5Ctimes%20log%28N/df_t%29%20%5C%5C%20%5C%5C%20%5Cindent%20N%20%3D%20the%5C%2Cnumber%5C%2Cof%5C%2Cdocuments%5C%2Cin%5C%2Cthe%5C%2Ccorpus%20%5C%5C%20%5Cindent%20df_t%20%3D%20number%5C%2Cof%20%5C%2Cdocuments%5C%2Cwhich%5C%2Ccontain%5C%2Cterm%5C%3At%20%5C%5C%20%5Cindent%20tf_%7Bt%2Cd%7D%20%3D%20frequency%5C%2Cof%5C%2Cterm%5C%3At%5C%3Ain%5C%2Cdocument%5C%3Ad%20%5C%5C%20%5Cindent%20idf_t%20%3D%20inverse%5C%2Cdocument%5C%2Cfrequence%5C%2Cof%5C%2Cterm%5C%3At%20%5C%5C%20%5Cindent%20w_%7Bt%2Cd%7D%20%3D%20tf%5C%21%5C%21-%5C%21%5C%21idf%5C%3Aweight%5C%2Cof%5C%2Ca%5C%2Cterm

[Cosine_Similarity]: https://latex.codecogs.com/gif.latex?cos%28%5Cvec%7Bq%7D%2C%5Cvec%7Bd%7D%29%3D%5Cfrac%7B%5Cvec%7Bq%7D%20%5Cbullet%20%5Cvec%7Bd%7D%7D%7B%7C%7C%5Cvec%7Bq%7D%7C%7C%5C%3A%7C%7C%5Cvec%7Bd%7D%7C%7C%7D%3D%5Cfrac%7B%5Csum_%7Bi%3D1%7D%5E%7B%7Cq%7C%7D%20q_i%5C%2Cd_i%7D%7B%5Csqrt%7B%5Csum_%7Bi%3D1%7D%5E%7B%7Cq%7C%7Dq_i%5E2%7D%5Csqrt%7B%5Csum_%7Bi%3D1%7D%5E%7B%7Cq%7C%7Dd_i%5E2%7D%7D%20%5C%5C%20%5Cvec%7Bq%7D%3Dquery%5C%2Cvector%20%5C%5C%20%5Cvec%7Bd%7D%3Ddocument%5C%2Cvector
