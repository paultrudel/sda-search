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


## Indexing the Content

Apache Lucene was used for the indexing and search purposes of this application.

### How Lucene Stores Information

Terminology:
- A SDADocument is a project specific database entity which models a web page
- A document refers to a Lucene specific term for a unit of indexing/search which is made up of a group of information fields  

For this project approximately 10,000 SDADocuments make up the document corpus. Each SDADocument contains a body of text and job of the index is to store information about this text content in a manner that allows for fast lookup of which documents contain a particular term. To do this Lucene stores this term-document information in an inverted index which maps each distinct term in the corpus to a list of documents, unqiuely identified by a serial number. Each entry in this associated list of documents is tuple containing the document's unqiue identified and the position in the document where the term is found. 
The advatage of storing information this way is that the memory needed for storage is very small compared to the actual size of a document's content and grows slowly as more documents are added to the index. The lookup time is also quite fast since it has a time complexity of approximately O(log n), where n is the number of distinct terms in the corpus. The process of building the document index also involves other common NLP techniques such as the dropping of stop words, stemming, and lemmatization.


## Retrieving the Content

The process of querying the index to search for relevant content is the most complex aspect of a search engine and the one that has the most variey in the way it can be approached. Large scale seach engines such as Google use hundreds of factors in their search process to determine what content is relevant to a user's search. The search engine in this project uses two factors, TF-IDF and PageRank scores.
