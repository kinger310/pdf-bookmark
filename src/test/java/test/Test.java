package test;

import com.ifnoelse.pdf.PDFUtil;

import java.io.IOException;

/**
 * Created by ifnoelse on 2017/2/25 0025.
 */
public class Test {
    public static void main(String[] args) throws IOException {

        //Get catalog information for books
        // String contents = PDFContents.getContentsByUrl("http://product.china-pub.com/223565");

        String contents = "第一篇\t论资本的性质及其蓄积和用途\t1\n" +
            "\t第十一章\t论地租\t140\n" +
            "\t\t第一节\t论总能提供地租的土地生产物\t142\n" +
            "\t\t第二节\t论有时提供有时不提供地租的土地生产物\t158\n" +
            "\t\t第三节\t论总能提供地租的生产物与有时提供有时不提供地租的生产物这二者价值比例的变动\t172\n" +
            "\t\t顺便谈谈前四世纪银价的变动\t174\n" +
            "\t\t\t第一期\t174\n" +
            "\t\t\t第二期\t188\n" +
            "\t\t\t第三期\t189\n" +
            "\t\t金银价值比例的变动\t206\n" +
            "\t\t怀疑银价仍在继续跌落的根据\t211\n" +
            "\t\t社会进步对三种原生产物的不同影响\t212\n" +
            "\t\t\t第一类\t213\n" +
            "\t\t\t第二类\t214\n" +
            "\t\t\t第三类\t224\n" +
            "\t\t关于银价变动的结论\t234\n" +
            "\t\t改良的进展对于制造品真实价格的影响\t239\n" +
            "\t\t本章的结论\t243\n" +
            "第二篇\t论资本的性质及其蓄积和用途\t258\n" +
            "\t序论\t259\n" +
            "\t第一章\t论资本的划分\t261";


        //Add a table of contents to a book
        PDFUtil.addBookmark(PDFUtil.generateBookmark(contents, 14), "Pdf path to add bookmarks", "Pdf output path after adding bookmark");


    }
}
