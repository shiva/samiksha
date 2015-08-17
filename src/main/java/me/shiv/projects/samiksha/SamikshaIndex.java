package me.shiv.projects.samiksha;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.RAMDirectory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class SamikshaIndex {	
	private RAMDirectory index;
	private StandardAnalyzer analyzer;
	private IndexSearcher searcher;
	private DirectoryReader reader;
	private int hitsPerPage;
	private IndexWriterConfig writeConfig;
	
	public boolean addOPMLToIndex(File f) {
		
		if ( f.isHidden() || 
			 f.isDirectory() || 
			 !f.canRead() || 
			 !f.exists() || 
			 !OpmlReader.isOpmlFile(f) ) {
	    
			return false;
	    }
		
	    try {
			System.out.println("Indexing file " + f.getCanonicalPath());
		    IndexWriter indexWriter = new IndexWriter(this.index, this.writeConfig);    

		    Document doc = new Document();
		    doc.add(new TextField("contents", new FileReader(f)));
		    doc.add(new StringField("filename", f.getCanonicalPath(), Field.Store.YES));
		    indexWriter.addDocument(doc);
		    indexWriter.close();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return true;
	}
	
	
	public SamikshaIndex() throws IOException {
		this.analyzer = new StandardAnalyzer();
	    this.index = new RAMDirectory();
	    
	    this.writeConfig = new IndexWriterConfig(this.analyzer);
	}
	
	public void findAndDisplay(String queryStr) throws ParseException, IOException {
		
		this.hitsPerPage = 10;
	    this.reader = DirectoryReader.open(this.index);
	    this.searcher = new IndexSearcher(this.reader);
	    
		String querystr = queryStr.length() > 0 ? queryStr : "lucene";		
	    Query q = new QueryParser("outline", analyzer).parse(querystr);

	    TopScoreDocCollector collector = TopScoreDocCollector.create(this.hitsPerPage);
	    
	    this.searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    
	    // 4. display results
	    System.out.println("Found " + hits.length + " hits.");
	    for(int i=0;i<hits.length;++i) {
	      int docId = hits[i].doc;
	      Document d = searcher.doc(docId);
	      System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
	    }		
	    this.reader.close();
	}
	
	public void printIndex() {
		
	}
}
