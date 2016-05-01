

package searchengine ;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;

public class SearchEngine {

    
    public static void main(String[] args) {
        indexDirectory();
        String userInput = "";
        Scanner getInputWord = new Scanner(System.in);

        System.out.print("Please type a word to search for.\nInput: ");
        userInput = getInputWord.nextLine();
        search(userInput);
        
        System.out.print("Search again? (type Y or N) Y = Yes, N = No: ");
        userInput = getInputWord.nextLine();
        while (!userInput.equals("n") && !userInput.equals("N")) {
            System.out.print("Input: ");
            userInput = getInputWord.nextLine();
            search(userInput);
            System.out.print("Search again? (Y or N): ");
            userInput = getInputWord.nextLine();
        }
    }

    private static void indexDirectory() {
        //Apache Lucene Indexing Directory .txt files     
        try {
            Path path;
            path = Paths.get("C:\\Users\\USER\\Desktop\\index");
            Directory directory = FSDirectory.open(path);
            
            IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());
            IndexWriter indexWriter = new IndexWriter(directory, config);
            indexWriter.deleteAll();
            File f = new File("C:\\seminar\\test_data"); // current directory     
            for (File file : f.listFiles()) {
                //System.out.println("indexed " + file.getCanonicalPath());
                Document doc = new Document();
                doc.add(new TextField("FileName", file.getName(), Store.YES));

                FileInputStream is = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer stringBuffer = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line).append("\n");
                }
                reader.close();
                doc.add(new TextField("contents", stringBuffer.toString(), Store.YES));
                indexWriter.addDocument(doc);
            }
            indexWriter.close();
            directory.close();
            System.out.println("indexing finished");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static void search(String text) {
        //Apache Lucene searching text inside .txt files
        try { //mohamed , mohamed nasser
            Path path = Paths.get("C:\\Users\\USER\\Desktop\\index");

            Directory directory = FSDirectory.open(path);
            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            
            String[] strArray = text.split(" ");// فصل لكلمة البحث 
            
            //Count words And Excute loop to searching for the string word by word 
           
          //luceen search for the word and get the files names that have the word
            int filescount = 0 ;// منتغير  يحسب عدد الملفات
            for(int g=0;g<strArray.length;g++){// لفرز كلمات المفصولة
            	
            	FuzzyQuery query = new FuzzyQuery(new Term("contents",strArray[g]), 2);//تنفيذ الكويري بالاندكس
                TopDocs topDocs = indexSearcher.search(query, 2); // يجيب عدد الملفات الموجود بها كلمة  البحث
            	
               
                if(topDocs.totalHits > 0){//شرط لاظافة او تغيير عدد الملفات توت هست بها اجمال الملفات الي بيها الكلمة  
                	
                	filescount = topDocs.totalHits;// هنا يحسب عدد الملفات التي بها الكلمة 
                      
                     
                	
                	
                }
            }
            
            
          

            int i = 0;
            //if luceen found the word it gonna get the file name for it 
            if (filescount > 0) {// شرط اذا كان عدد الملفات اكبر من صفر يتم تنفيذ البحث
                System.out.println("Found " + filescount + " result(s).");
                
                for (int b = 0;b<filescount;b++) {//يبحث عن الكلمات داخل كل ملف ع حدى
                
                    Document document = indexSearcher.doc(b);//نعرف اسم الملف طبقا بالرقم 
                    System.out.println("Result #" + i + " " + document.get("FileName"));// طبع عدد الملفات
                    i++; 
                 Scanner txtscan = new Scanner(new File("C:\\seminar\\test_data\\"+document.get("FileName")));// فتح الملف للقراءة 
            int line = 0;      
       while(txtscan.hasNextLine()){//يبحث داخل كل سطر داخل الملف الي احنا حددناه 
    	   	
    	    String str = txtscan.nextLine();//السطر المقروء اول سطر يقراه  يلف ع الملف لحد ما يقره سطر يطر
    	   
    	    String print = "" ;// متغير يحمل قيمة السطر بعد تغيير وضع القوسيين  
    	   for(int wl=0;wl<strArray.length;wl++){//ابحث عن الكلام الي انا ابحث  عنوكلمة كلمة داخل  سطر سطر  
    		  
    		   String word = strArray[wl];//هنا يخزن كلمة البحث في المصفوفه 
    		   
    		   if(str.indexOf(word) != -1){// تبحث عن الكلمة داخل السطر 
    			   str = str.replace(word, "("+word+")");// وضع القوسين للكلمات المبحوثه عنهااوتبديلها بالقوسين 
    			  print = str;// متغير للطباعه 
    		   }
    		   
    		   
    	   }
    	   if(print!=""){ System.out.println(print);}// هنا الطباعه السطر خارج اللوب بسبب عدم تكرار طباعه السطر نفسه

                
                }
                }
            }
                
             else { // اذا عدد الملفات تساوي صفر يطبع لايوجد نتيجه 
                System.out.println("No maches found!");
            }
         }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}

