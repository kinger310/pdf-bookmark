package com.ifnoelse.pdf;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by ifnoelse on 2017/2/25 0025.
 */
public class PDFUtil {
    private static Pattern bookmarkPattern = Pattern.compile("^([\t\\s　]*)?([0-9.]+)?(.*?)/?[\t\\s　]*([-?0-9]+)[\t\\s　]*?$");
    private static String blankRegex = "[\t\\s　]";

    public static String replaceBlank(String str) {
        return str.replaceAll(blankRegex, " ");
    }

    public static void addBookmark(String bookmarks, String srcFile, String destFile, int pageIndexOffset) {

        if (bookmarks != null && !bookmarks.isEmpty()) {
            if (bookmarks.trim().startsWith("http")) {
                addBookmark(PDFContents.getContentsByUrl(bookmarks), srcFile, destFile, pageIndexOffset);
            } else {
                addBookmark(Arrays.asList(bookmarks.split("\n")), srcFile, destFile, pageIndexOffset);
            }
        }
    }


    public static List<Bookmark> generateBookmark(String bookmarks, int pageIndexOffset, int minLens, int maxLnes) {
        return generateBookmark(Arrays.asList(bookmarks.split("\n")), pageIndexOffset, minLens, maxLnes);
    }

    public static List<Bookmark> generateBookmark(String bookmarks, int pageIndexOffset) {
        return generateBookmark(Arrays.asList(bookmarks.split("\n")), pageIndexOffset, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Add a directory to the pdf file
     *
     * @param bookmarks       Directory content, each list element is a directory content, such as：“1.1 Functional vs. Imperative Data Structures 1”
     * @param pageIndexOffset The pdf file is really the offset between the page number and the directory page number.
     * @param minLens         Legal directory entry minimum length
     * @param maxLens         Legal directory entry maximum length
     * @return Returns a list of bookmarked content
     */
    public static List<Bookmark> generateBookmark(List<String> bookmarks, int pageIndexOffset, int minLens, int maxLens) {
        List<Bookmark> bookmarkList = new ArrayList<>();
        LinkedList<Bookmark> stack = new LinkedList<>();  // 使用LinkedList来作为栈

        for (String ln : bookmarks) {
            ln = replaceBlank(ln);
            if (ln.length() < minLens || ln.length() > maxLens) continue;
            Matcher matcher = bookmarkPattern.matcher(ln);
            if (matcher.find()) {
                int currentIndent = matcher.group(1).length();
                String seq = matcher.group(2);
                String title = replaceBlank(matcher.group(3));
                int pageIndex = Integer.parseInt(matcher.group(4));

                // 创建当前书签对象
                Bookmark currentBookmark = new Bookmark(seq, title, pageIndex + pageIndexOffset, currentIndent);
                if (title.equals("第二篇 论资本的性质及其蓄积和用途 ")) {
                    System.out.println(title);
                }
                // 如果栈为空，直接加入书签列表
                if (stack.isEmpty()) {
                    bookmarkList.add(currentBookmark);
                    stack.addFirst(currentBookmark);  // 将当前书签加入栈顶
                } else {
                    // 检查栈顶的书签
                    while (!stack.isEmpty() && stack.peekFirst().getIndent() >= currentIndent) {
                        stack.removeFirst();  // 弹出比当前缩进大或相等的书签
                    }

                    if (!stack.isEmpty()) {
                        // 将当前书签作为栈顶书签的子书签
                        stack.peekFirst().addSubBookmark(currentBookmark);
                    } else {
                        // 没有符合条件的父书签，直接加入书签列表
                        bookmarkList.add(currentBookmark);
                    }

                    // 将当前书签压入栈中
                    stack.addFirst(currentBookmark);
                }

            } else {
                throw new IllegalArgumentException(ln + " 输入格式不正确。请使用[缩进] [章节序号（可选）] [章节标题] [页码]");
            }
        }
        return bookmarkList;
    }


    public static void addBookmark(List<String> bookmarks, String srcFile, String destFile, int pageIndexOffset, int minLens, int maxLnes) {
        addBookmark(generateBookmark(bookmarks, pageIndexOffset, minLens, maxLnes), srcFile, destFile);
    }

    public static void addBookmark(List<String> bookmarks, String srcFile, String destFile, int pageIndexOffset) {
        addBookmark(bookmarks, srcFile, destFile, pageIndexOffset, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static void addBookmark(Bookmark bookmark, String srcFile, String destFile) {
        addOutlines(Arrays.asList(bookmark.outlines()), srcFile, destFile);
    }

    public static void addBookmark(List<Bookmark> bookmarks, String srcFile, String destFile) {
        addOutlines(bookmarks.stream().map(Bookmark::outlines).collect(Collectors.toList()), srcFile, destFile);
    }

    private static void addOutlines(List<HashMap<String, Object>> outlines, String srcFile, String destFile) {
        try {
            class MyPdfReader extends PdfReader {
                public MyPdfReader(String fileName) throws IOException {
                    super(fileName);
                    unethicalreading = true;
                    encrypted = false;
                }
            }
            PdfReader reader = new MyPdfReader(srcFile);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destFile));
            stamper.setOutlines(outlines);
            stamper.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}