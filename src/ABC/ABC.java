/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ABC;

import NMF.*;
import java.util.Iterator;
import tml.corpus.CorpusParameters;
import tml.vectorspace.TermWeighting.GlobalWeight;
import tml.vectorspace.TermWeighting.LocalWeight;
import tml.corpus.SearchResultsCorpus;
import tml.storage.Repository;
import tml.utils.Stats;
import tml.vectorspace.operations.PassagesSimilarity;

/**
 *
 *
 *
 * @author Holus
 */
public class ABC {

    Variables var;

    public ABC() throws Exception {
        var = new Variables();
       // trainData("data");
    }

    /**
     * @param args the command line arguments
     */
    public String trainData(String ans) throws Exception {

        Repository repository = new Repository("C:/tml-3.2/tml/corpora/myrepo");
        repository.cleanStorage("C:/tml-3.2/tml/corpora/myrepo");
        repository.addDocumentsInFolder(ans);
        var.setCollection(testData());
        System.out.println(testData());
        return testData();

        // TODO code application logic here
    }

    public String testData() throws Exception {
        Repository repository = new Repository("C:/tml-3.2/tml/corpora/myrepo");
        SearchResultsCorpus corpus = new SearchResultsCorpus("type:document");
        corpus.getParameters().setTermSelectionThreshold(0);
        corpus.getParameters().setDimensionalityReduction(CorpusParameters.DimensionalityReduction.NO);
        corpus.getParameters().setDimensionalityReductionThreshold(50);
        corpus.getParameters().setTermWeightGlobal(GlobalWeight.None);
        corpus.getParameters().setTermWeightLocal(LocalWeight.TF);
        corpus.load(repository);
        // System.out.println("Corpus Loaded and Semantic space Calculated");
        // System.out.println("Total documents: "+corpus.getPassages().length);

        System.out.println(corpus.parametersSummary());
        PassagesSimilarity distances = new PassagesSimilarity();
        distances.setCorpus(corpus);
        distances.start();
        String a = "";
        for (String str : corpus.getPassages()) {
            a += str + "\n";
        }
        var.setAnswer(a);
        var.setWordFreq(corpus.printFrequencies());
        a = "U\n";
        for (double dob[] : corpus.getTermDocMatrix().svd().getU().getArray()) {
            for (double dob1 : dob) {
                a += dob1 + " ";
            }
            a += "\n";
        }
        var.setU(a);
        a = "V\n";
        for (double dob[] : corpus.getTermDocMatrix().svd().getV().transpose().getArray()) {
            for (double dob1 : dob) {
                a += dob1 + " ";
            }
            a += "\n";
        }
        var.setV(a);
        a = "S\n";
        for (double dob[] : corpus.getTermDocMatrix().svd().getS().transpose().getArray()) {
            for (double dob1 : dob) {
                a += dob1 + " ";
            }
            a += "\n";
        }
        var.setE(a);
        a = "A\n";
        for (double dob[] : corpus.getTermDocMatrix().getArray()) {
            for (double dob1 : dob) {
                a += dob1 + " ";
            }
            a += "\n";
        }
        var.setA(a);
        a = "R\n" + distances.getResultsCSVString();

        String a1[] = a.split("\n");
        String y = "";
        for (String string : a1) {
            if (string.startsWith("lecturer")) {
                y += string + "\n";
            }
        }
        var.setRel("R\n" + y);
        //distances.printResults();
        int i = 1;
        System.out.println(corpus.getName());
        for (String string : corpus.getTerms()) {
            System.out.println(i++ + string);
        }
        i = 1;
        for (Stats string : corpus.getDocStats()) {
            System.out.println(i++ + string.toString());
        }
        i = 1;
        for (Stats string : corpus.getTermStats()) {
             System.out.println(corpus.getTerms()[i-1]);
            System.out.println(i++ + string.toString());
        }
        for (String string[] : distances.getResultsStringTable()) {
            System.out.println();
            for (String string1 : string) {
            }
                
        }
        
       
        
        i = 1;
        System.out.println("===>" + corpus.parametersSummary());



       // return null;
         return var.getAnswer() + "\n"+ "\n" + var.getWordFreq() + "\n" + "\n" + corpus.parametersSummary() + "\n" + "\n";
    }

//    public String rep() throws Exception {
//        Repository repository = new Repository("C:/tml-3.2/tml/corpora/myrepo");
//        SearchResultsCorpus corpus = new SearchResultsCorpus("type:document");
//        corpus.load(repository);
//        String a = "";
//        for (String str : corpus.getPassages()) {
//            a += str + "\n";
//        }
//        var.setAnswer(a);
//        return a;
//    }
    public static void main(String[] args) throws Exception {
        new ABC();
    }
}
