package org.lucenewebsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;


public class SearchFiles {
	
	public static TopDocs search(String indexPath, String query) {
		return search(indexPath, query, false);
	}
	
	public static TopDocs search(String indexPath, String query, boolean displayResults) {
		String[] fields = {"title", "contents"};

		final int maxHitsDisplay = 10;	// Maximum number of result documents to display

		TopDocs results = null;

		try {
			// Initialize the index reader
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));

			// Initialize the index searcher
			IndexSearcher searcher = new IndexSearcher(reader);

			Analyzer analyzer = new StandardAnalyzer();
			
			// MultifiedQueryParser can search multiple fields in the document objects
			// using the same parser instance. 
			MultiFieldQueryParser mfparser = new MultiFieldQueryParser(fields, analyzer);

			// Parse the query and store it in a Query Object
			Query q = mfparser.parse(query);
			
			System.out.println("\nSearching For: " + query + "\n");
			
			// Call the method that executes the search
			results = searcher.search(q, maxHitsDisplay);
			
			if( displayResults ) {
				displayResults(searcher, results);
			}

			// Close the IndexReader
			reader.close();
		} 
		catch(Exception e) {
			System.out.println("Error while initializing index reader " + e);
		}
		
		return results;
	}
	
	
	/**
	 * debug
	*/
	public static void displayResults(IndexSearcher searcher, TopDocs results) throws IOException {
		Date startDate = new Date();
		
		// Collect hits
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = Math.toIntExact(results.totalHits);

		Date endDate = new Date();
		
		System.out.println("Total " + numTotalHits + " Matching Documents Found in " + ((endDate.getTime() - startDate.getTime()) / 1000.0) + " Seconds");
		
		// Iterate over the hits array
		for (int i = 0; i < numTotalHits; i++) {

			Document doc = searcher.doc(hits[i].doc);

			String title = doc.get("title");
			String url = doc.get("url");
			double score = 	hits[i].score;

			if (url != null) {
				// prints the document rank and title 1. Manchester United.html
				System.out.println((i+1) + ". " + title);

				// prints the path of the document
				System.out.println("   Url: " + url);

				// prints document score
				System.out.println("   Score: " + score + "\n");
				
			} else {
				System.out.println((i+1) + ". " + "Document does not exist");
			}
		}
	}
}
