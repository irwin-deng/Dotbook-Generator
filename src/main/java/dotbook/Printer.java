/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dotbook;

/**
 *
 * @author Irwin
 */
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfSmartCopy;
import static dotbook.UserInterface.PERFORMER_COUNT;

import java.io.*;
import java.util.ArrayList;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.io.IOUtils;

public class Printer {

    public static String dest = "untitled.pdf";
    public static final String SRC1 = "template1.pdf";
    public static final String SRC2 = "template2.pdf";
    public static final String SRC3 = "template3.pdf";
    public static final String SRC4 = "template4.pdf";

    public static final float U = 17.5f;
    public static final float X_ORIG = 185.6f;
    public static final float Y_ORIG = 10.2f;

    public static ArrayList<String> pdfList;

    //public static void print(String title, String id, String set, String measures, String action, String side, String[] pos, double hCoord, double vCoord, int leftBound, int botBound)
    public static void print(int pIndx, int sIndx, int eIndx) throws Exception {
        pdfList = new ArrayList<>();
        Performer p = UserInterface.performers[pIndx];
        dest = "dotbook_output/DotBook_" + p.id + "_" + p.sets[sIndx].num + (p.sets[sIndx].sub == '-' ? "" : p.sets[sIndx].sub) + "-" + p.sets[eIndx].num + (p.sets[eIndx].sub == '-' ? "" : p.sets[eIndx].sub) + ".pdf";
        File out_dir = new File("dotbook_output/");
        out_dir.mkdirs();
        File sets_dir = new File("dotbook_output/individual_sets/");
        sets_dir.mkdirs();
        for (int i = sIndx; i <= eIndx; i++) {
            Set s = p.sets[i];
            int[] window = getBestView(UserInterface.performers[pIndx], i);
            int leftBound = window[0];
            int botBound = window[1];
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(getSource(botBound));
            PdfReader reader = new PdfReader(in);
            String file = "dotbook_output/individual_sets/db_" + p.id + "_" + p.sets[i].num + (p.sets[i].sub == '-' ? "" : p.sets[i].sub) + ".pdf";
            pdfList.add(file);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(file));

            PdfContentByte canvas = stamper.getOverContent(1);
            byte[] cBytes = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("comic.ttf"));
            BaseFont comicSans = BaseFont.createFont("comic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, cBytes, null);

            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase(s.title, new Font(comicSans, 24)), 420, 566, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase(p.id, new Font(comicSans, 24)), 440, 535, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase("" + s.num + (s.sub == '-' ? "" : s.sub), new Font(comicSans, 24)), 100, 542, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase((s.startMeas == 999 ? "End" : s.startMeas) + (s.startMeas == s.endMeas ? "" : "-" + (s.endMeas == 999 ? "End" : s.endMeas)), new Font(comicSans, 24)), 78, 464, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase((s.counts != 0 ? (s.move ? "Move " : "Hold ") + s.counts : ""), new Font(comicSans, 24)), 78, 384, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase(s.coords.side == 0 ? "" : Integer.toString(s.coords.side), new Font(comicSans, 24)), 725, 539, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase(s.form, new Font(comicSans, 24)), 74, 304, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase("•", new Font(comicSans, 100)), X_ORIG + U * ((float) s.coords.hCoord - leftBound), Y_ORIG + U * ((float) s.coords.vCoord - botBound), 0);

            String[] pos = formatPos(s.coords.hAdj, Integer.toString(s.coords.yardLine), s.coords.vAdj, s.coords.hashLine);
            for (int j = 0; j < pos.length; j++) {
                ColumnText.showTextAligned(canvas,
                        Element.ALIGN_CENTER, new Phrase(pos[j], new Font(comicSans, 20)), 714, 503 - 36 * j, 0);
            }

            byte[] tBytes = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("times.ttf"));
            BaseFont times = BaseFont.createFont("times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, tBytes, null);
            Font font = new Font(times, 60, Font.NORMAL, BaseColor.LIGHT_GRAY);
            Phrase ph1 = new Phrase();
            Phrase ph2 = new Phrase();
            ph1.add(new Chunk("\u2192", font));
            ph2.add(new Chunk("\u2192", font));

            double pAng = s.prevDirection;
            double nAng = s.nextDirection;

            //for (int t = 0; t < 360; t += 72) {
            double[] adjst = {-39, -15};
            double[] adjst1 = {39, -15};
            //    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, ph1, X_ORIG + (float) adjst[0] + U * (float) (s.coords.hCoord - leftBound), Y_ORIG + (float) adjst[1] + 36.8f + U * ((float) s.coords.vCoord - botBound), t);
            //}
            if (s.move) {
                ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, ph1, X_ORIG + (float) rotate(adjst, pAng)[0] + U * ((float) s.coords.hCoord - leftBound), Y_ORIG + 36.8f + (float) rotate(adjst, pAng)[1] + U * ((float) s.coords.vCoord - botBound), (float) pAng);
            }
            if (i < 65 && p.sets[i + 1].move) {
                ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, ph2, X_ORIG + (float) rotate(adjst1, nAng)[0] + U * ((float) s.coords.hCoord - leftBound), Y_ORIG + 36.8f + (float) rotate(adjst1, nAng)[1] + U * ((float) s.coords.vCoord - botBound), (float) nAng);
            }
            
            ArrayList<Integer> indxs = getOtherPerformers(leftBound, botBound, i);
            for (int j = 0; j < indxs.size(); j++) {
                if (indxs.get(j) != pIndx) {
                    ColumnText.showTextAligned(canvas,
                            Element.ALIGN_CENTER, new Phrase("•", new Font(comicSans, 70, Font.NORMAL, BaseColor.GRAY)), X_ORIG + U * ((float) UserInterface.performers[indxs.get(j)].sets[i].coords.hCoord - leftBound), Y_ORIG + 11.5f + U * ((float) UserInterface.performers[indxs.get(j)].sets[i].coords.vCoord - botBound), 0);
                    ColumnText.showTextAligned(canvas,
                            Element.ALIGN_CENTER, new Phrase(UserInterface.performers[indxs.get(j)].id, new Font(comicSans, 20, Font.NORMAL, BaseColor.GRAY)), X_ORIG + U * ((float) UserInterface.performers[indxs.get(j)].sets[i].coords.hCoord - leftBound), Y_ORIG + 12f + U * ((float) UserInterface.performers[indxs.get(j)].sets[i].coords.vCoord - botBound), 0);
                }
            }

            for (int j = 0; j < 4; j++) {
                ColumnText.showTextAligned(canvas,
                        Element.ALIGN_CENTER, new Phrase(Integer.toString(leftBound * 5 / 8 + 5 * j > 50 ? 100 - (leftBound * 5 / 8 + 5 * j) : leftBound * 5 / 8 + 5 * j), new Font(comicSans, 24)), 184 + 8 * U * j, 486, 0);
            }

            stamper.close();
            reader.close();
        }

        Document document = new Document();
        PdfCopy copy = new PdfSmartCopy(document, new FileOutputStream(dest));
        document.open();
        for (int l = 0; l < pdfList.size(); l++) {
            PdfReader reader1 = new PdfReader(pdfList.get(l));
            copy.addDocument(reader1);
            reader1.close();
        }
        document.close();

    }

    public static double[] rotate(double[] ary, double deg) {
        double[] z = {Math.cos(Math.toRadians(deg)) * ary[0] - Math.sin(Math.toRadians(deg)) * ary[1], Math.sin(Math.toRadians(deg)) * ary[0] + Math.cos(Math.toRadians(deg)) * ary[1]};
        return z;
    }

    public static void main(String[] args) throws Exception {
        int pIndx = 0;
        int sIndx = 0;
        int eIndx = 65;
        Performer p = UserInterface.performers[pIndx];
        File out_dir = new File("dotbook_output/");
        out_dir.mkdirs();
        dest = "dotbook_output/DotBook_" + p.id + "_" + p.sets[sIndx].num + (p.sets[sIndx].sub == '-' ? "" : p.sets[sIndx].sub) + "-" + p.sets[eIndx].num + (p.sets[eIndx].sub == '-' ? "" : p.sets[eIndx].sub) + ".pdf";
        for (int i = sIndx; i <= eIndx; i++) {
            Set s = p.sets[i];
            int[] window = getBestView(UserInterface.performers[pIndx], i);
            int leftBound = window[0];
            int botBound = window[1];
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(getSource(botBound));
            PdfReader reader = new PdfReader(in);
            String file = "dotbook_output/individualSets/db_" + p.id + "_" + p.sets[i].num + (p.sets[i].sub == '-' ? "" : p.sets[i].sub) + ".pdf";
            pdfList.add(file);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(file));

            PdfContentByte canvas = stamper.getOverContent(1);
            BaseFont comicSans = BaseFont.createFont("comic.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);

            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase(s.title, new Font(comicSans, 24)), 420, 566, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase(p.id, new Font(comicSans, 24)), 440, 535, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase("" + s.num + (s.sub == '-' ? "" : s.sub), new Font(comicSans, 24)), 100, 542, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase((s.startMeas == 999 ? "End" : s.startMeas) + (s.startMeas == s.endMeas ? "" : "-" + (s.endMeas == 999 ? "End" : s.endMeas)), new Font(comicSans, 24)), 78, 464, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase((s.move ? "Move " : "Hold ") + s.counts, new Font(comicSans, 24)), 78, 384, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase(s.coords.side == 0 ? "" : Integer.toString(s.coords.side), new Font(comicSans, 24)), 725, 539, 0);
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase("•", new Font(comicSans, 100)), X_ORIG + U * ((float) s.coords.hCoord - leftBound), Y_ORIG + U * ((float) s.coords.vCoord - botBound), 0);

            String[] pos = formatPos(s.coords.hAdj, Integer.toString(s.coords.yardLine), s.coords.vAdj, s.coords.hashLine);
            for (int j = 0; j < pos.length; j++) {
                ColumnText.showTextAligned(canvas,
                        Element.ALIGN_CENTER, new Phrase(pos[j], new Font(comicSans, 20)), 714, 505 - 36 * j, 0);
            }

            ArrayList<Integer> indxs = getOtherPerformers(leftBound, botBound, i);
            for (int j = 0; j < indxs.size(); j++) {
                if (indxs.get(j) != pIndx) {
                    ColumnText.showTextAligned(canvas,
                            Element.ALIGN_CENTER, new Phrase("•", new Font(comicSans, 70, Font.NORMAL, BaseColor.GRAY)), X_ORIG + U * ((float) UserInterface.performers[indxs.get(j)].sets[i].coords.hCoord - leftBound), Y_ORIG + 11.5f + U * ((float) UserInterface.performers[indxs.get(j)].sets[i].coords.vCoord - botBound), 0);
                    ColumnText.showTextAligned(canvas,
                            Element.ALIGN_CENTER, new Phrase(UserInterface.performers[indxs.get(j)].id, new Font(comicSans, 20, Font.NORMAL, BaseColor.GRAY)), X_ORIG + U * ((float) UserInterface.performers[indxs.get(j)].sets[i].coords.hCoord - leftBound), Y_ORIG + 12f + U * ((float) UserInterface.performers[indxs.get(j)].sets[i].coords.vCoord - botBound), 0);
                }
            }

            for (int j = 0; j < 4; j++) {
                ColumnText.showTextAligned(canvas,
                        Element.ALIGN_CENTER, new Phrase(Integer.toString(leftBound * 5 / 8 + 5 * j > 50 ? 100 - (leftBound * 5 / 8 + 5 * j) : leftBound * 5 / 8 + 5 * j), new Font(comicSans, 24)), 185 + 8 * U * j, 486, 0);
            }

            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_CENTER, new Phrase("→", new Font(comicSans, 24, Font.NORMAL, BaseColor.GRAY)), 185, 486, 0);

            stamper.close();
            reader.close();
        }
    }

    public static String[] formatPos(double hAdj, String yardLine, double vAdj, String hashLine) {
        String h;
        if (Math.abs(hAdj) == 1) {
            h = (hAdj != 0 ? (hAdj > 0 ? (int) hAdj + " step inside " : -(int) hAdj + " step outside ") : "On ") + yardLine;
        } else {
            if (hAdj % 1 == 0) {
                h = (hAdj != 0 ? (hAdj > 0 ? (int) hAdj + " steps inside " : -(int) hAdj + " steps outside ") : "On ") + yardLine;
            } else {
                h = (hAdj != 0 ? (hAdj > 0 ? hAdj + " steps inside " : -hAdj + " steps outside ") : "On ") + yardLine;
            }
        }
        String[] hAry;

        if (h.length() > 5) {
            hAry = new String[2];
            hAry[0] = h.split("\\s+")[0] + " " + h.split("\\s+")[1];
            hAry[1] = h.split("\\s+")[2] + " " + h.split("\\s+")[3];
        } else {
            hAry = new String[1];
            hAry[0] = "On " + yardLine;
        }

        String v;

        if (Math.abs(vAdj) == 1) {
            v = (vAdj != 0 ? (vAdj > 0 ? (int) vAdj + " step in front of " : -(int) vAdj + " step behind ") : "On ") + hashLine;
        } else {
            if (vAdj % 1 == 0) {
                v = (vAdj != 0 ? (vAdj > 0 ? (int) vAdj + " steps in front of " : -(int) vAdj + " steps behind ") : "On ") + hashLine;
            } else {
                v = (vAdj != 0 ? (vAdj > 0 ? vAdj + " steps in front of " : -vAdj + " steps behind ") : "On ") + hashLine;
            }
        }

        String[] vAry;
        if (v.length() < 12) {
            vAry = new String[1];
            vAry[0] = v;
        } else {
            String[] t = findBestArrangement2(v);
            int longest = -1;
            for (int i = 0; i < 2; i++) {
                if (t[i].length() > longest) {
                    longest = t[i].length();
                }
            }
            if (longest < 12) {
                vAry = new String[2];
                vAry[0] = t[0];
                vAry[1] = t[1];

            } else {
                vAry = new String[3];
                String[] u = findBestArrangement3(v);
                vAry[0] = u[0];
                vAry[1] = u[1];
                vAry[2] = u[2];
            }
        }

        String[] both = (String[]) ArrayUtils.addAll(hAry, vAry);
        return both;
    }

    public static String[] findBestArrangement2(String s) {
        int score = 99;
        String[] best = new String[2];
        ArrayList<Integer> spaces = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 32) {
                spaces.add(i);
            }
        }
        for (int i = 0; i < spaces.size(); i++) {

            if (Math.abs(s.substring(0, spaces.get(i)).length() - s.substring(spaces.get(i) + 1).length()) <= score) {

                best[0] = s.substring(0, spaces.get(i));
                best[1] = s.substring(spaces.get(i) + 1);
            }
        }

        return best;
    }

    public static String[] findBestArrangement3(String s) {
        double score = 99;
        String[] best = new String[3];
        ArrayList<Integer> spaces = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 32) {
                spaces.add(i);
            }
        }
        for (int i = 0; i < spaces.size() - 1; i++) {
            for (int j = i + 1; j < spaces.size(); j++) {
                double a = s.substring(0, spaces.get(i)).length();
                double b = s.substring(spaces.get(i) + 1, spaces.get(j)).length();
                double c = s.substring(spaces.get(j) + 1).length();
                double m = ((double) a + b + c) / 3;
                if (Math.sqrt((Math.pow(a - m, 2) + Math.pow(b - m, 2) + Math.pow(c - m, 2)) / 3) <= score) {

                    best[0] = s.substring(0, spaces.get(i));
                    best[1] = s.substring(spaces.get(i) + 1, spaces.get(j));
                    best[2] = s.substring(spaces.get(j) + 1);
                    score = Math.sqrt((Math.pow(a - m, 2) + Math.pow(b - m, 2) + Math.pow(c - m, 2)) / 3);
                }
            }
        }

        return best;
    }

    public static int longestString(String[] ary) {
        int a = -1;
        for (String ary1 : ary) {
            if (ary1.length() > a) {
                a = ary1.length();
            }
        }
        return a;
    }

    public static int[] getBestView(Performer p, int s) {
        int[] botCoord = {0, 4, 12, 20, 28, 32, 40, 48, 56, 60};
        int[] leftCoord = {0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 80, 88, 96, 104, 112, 120, 128, 136};

        double x = p.sets[s].coords.hCoord;
        double y = p.sets[s].coords.vCoord;

        double highScore = -1;
        int bestLBCoords[] = new int[2];
        bestLBCoords[0] = -1;
        bestLBCoords[1] = -1;

        for (int i = 0; i < leftCoord.length; i++) {
            for (int j = 0; j < botCoord.length; j++) {
                int bot = botCoord[j];
                int top = bot + 24;
                int left = leftCoord[i];
                int right = left + 24;
                if (bot <= y && top >= y && left <= x && right >= x) {
                    double centralization = Math.pow(Math.min(x - left, right - x), 2) + Math.pow(Math.min(y - bot, top - y), 2);

                    double people = 0;
                    double peopleCentralization = 0;
                    for (int k = 0; k < PERFORMER_COUNT; k++) {
                        if (UserInterface.performers[k].sets[s].coords.hCoord >= left && UserInterface.performers[k].sets[s].coords.hCoord <= right && UserInterface.performers[k].sets[s].coords.vCoord >= bot && UserInterface.performers[k].sets[s].coords.vCoord <= top) {
                            people += 1.5;
                            peopleCentralization += .05 * (Math.pow(Math.min(UserInterface.performers[k].sets[s].coords.hCoord - left, right - UserInterface.performers[k].sets[s].coords.hCoord), 1.5) + Math.pow(Math.min(UserInterface.performers[k].sets[s].coords.vCoord - bot, top - UserInterface.performers[k].sets[s].coords.vCoord), 1.5));
                        }
                    }
                    double score = centralization + people + peopleCentralization;
                    if (score > highScore) {
                        highScore = score;
                        bestLBCoords[0] = leftCoord[i];
                        bestLBCoords[1] = botCoord[j];
                    }
                }
            }
        }
        return bestLBCoords;
    }

    public static ArrayList<Integer> getOtherPerformers(int left, int bot, int s) {
        int top = bot + 24;
        int right = left + 24;
        {
            ArrayList<Integer> a = new ArrayList();
            for (int k = 0; k < PERFORMER_COUNT; k++) {
                if (UserInterface.performers[k].sets[s].coords.hCoord >= left && UserInterface.performers[k].sets[s].coords.hCoord <= right && UserInterface.performers[k].sets[s].coords.vCoord >= bot && UserInterface.performers[k].sets[s].coords.vCoord <= top) {
                    a.add(k);
                }
            }
            return a;
        }
    }

    public static String getSource(int bBound) {
        int temp = bBound % 28;
        if (temp == 0) {
            return SRC4;
        }
        if (temp == 4) {
            return SRC1;
        }
        if (temp == 12) {
            return SRC2;
        }
        if (temp == 20) {
            return SRC3;
        }
        System.out.println("Invalid bottom bound!");
        return "";
    }
}

/*
    public void PlaceChunck(String text, int x, int y) {
    PdfContentByte cb = writer.DirectContent;
    BaseFont bf = BaseFont.CreateFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    cb.SaveState();
    cb.BeginText();
    cb.MoveText(x, y);
    cb.SetFontAndSize(bf, 12);
    cb.ShowText(text);
    cb.EndText();
    cb.RestoreState();
}
}
    public static void absText(String text, int x, int y) {
        try {
            PdfContentByte cb = writer.getDirectContent();
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            cb.saveState();
            cb.beginText();
            cb.moveText(x, y);
            cb.setFontAndSize(bf, 12);
            cb.showText(text);
            cb.endText();
            cb.restoreState();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 */
