package com.overseas.mtpay.print;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.nexgo.oaf.apiv3.device.printer.AlignEnum;
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener;
import com.nexgo.oaf.apiv3.device.printer.Printer;
import com.cloudpos.jniinterface.PrinterInterface;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.device.DeviceManager;
import com.wizarpos.common.device.printer.KeywordTrigger;

import java.io.UnsupportedEncodingException;

public class PrinterHelper {

    public static final int BIT_WIDTH = 384;
    private static Printer printerN3N5;
    private static final int WIDTH = 48;
    private static final int GSV_HEAD = 8;
    private static final int FONT_SIZE_NORMAL = 24;

    private static String[] keywords = {
            "<b>", "</b>",        // 加粗
            "<c>", "</c>",        // 居中
            "<w>", "</w>",        // 加宽
            "<h>", "</h>",        // 加高
            "<l>", "</l>",        // 居左
            "<r>", "</r>",        // 居右
            "<bc>", "</bc>",    // 一维码
            "<qc>", "</qc>",    // 二维码
            "<br/>",            // 换行
            "<t/>",                // tab
            "<ul>", "</ul>",    // 下划线
            "<img>", "</img>",    // 图片
            "<1>", "</1>",        // 1倍字体
            "<2>", "</2>",        // 2倍字体
            "<3>", "</3>",        // 3倍字体
            "<4>", "</4>",        // 4倍字体
            "<5>", "</5>",        // 5倍字体
            "<6>", "</6>",        // 6倍字体
            "<7>", "</7>",        // 7倍字体
            "<8>", "</8>",        // 8倍字体
            "<s>", "</s>",        // 小字体
            "<sls>", "</sls>",     // 小行间距
            "<end/>",                 // 打印完成
            "<nbr/>"                  //N3或者N5的换行
    };

    public static void printBitmap(Bitmap bitmap) {
        try {
            PrinterInterface.open();
            PrinterInterface.begin();
            printerWrite(PrinterCommand.init());
            printerWrite(PrinterCommand.setHeatTime(180));

            byte[] result = generateBitmapArrayGSV_MSB(bitmap, 0, 0);
            int lines = (result.length - GSV_HEAD) / WIDTH;
            System.arraycopy(new byte[]{0x1D, 0x76, 0x30, 0x00, 0x30, 0x00, (byte) (lines & 0xff), (byte) ((lines >> 8) & 0xff)}, 0, result, 0, GSV_HEAD);

            printerWrite(result);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            PrinterInterface.end();
            PrinterInterface.close();
        }
    }

    /**
     * generate the MSB buffer for bitmap printing GSV command
     *
     * @param bm            the android's Bitmap data
     * @param bitMarginLeft the left white space in bits.
     * @param bitMarginTop  the top white space in bits.
     * @return buffer with DC2V_HEAD + image length
     */
    private static byte[] generateBitmapArrayGSV_MSB(Bitmap bm, int bitMarginLeft, int bitMarginTop) {
        byte[] result = null;
        int n = bm.getHeight() + bitMarginTop;
        int offset = GSV_HEAD;
        result = new byte[n * WIDTH + offset];
        for (int y = 0; y < bm.getHeight(); y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
                if (x + bitMarginLeft < BIT_WIDTH) {
                    int color = bm.getPixel(x, y);
                    int alpha = Color.alpha(color);
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    if (alpha > 128 && (red < 128 || green < 128 || blue < 128)) {
                        // set the color black
                        int bitX = bitMarginLeft + x;
                        int byteX = bitX / 8;
                        int byteY = y + bitMarginTop;
                        result[offset + byteY * WIDTH + byteX] |= (0x80 >> (bitX - byteX * 8));
                    }
                } else {
                    // ignore the rest data of this line
                    break;
                }
            }
        }
        return result;
    }


    public static void print(String text) {
        if (DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_N3_OR_N5) {
            printerN3N5 = App.getInstance().deviceEngine.getPrinter();
            printerN3N5.setTypeface(Typeface.DEFAULT);
            printerN3N5.initPrinter();
            printerN3N5.setLetterSpacing(5);
            KeywordTrigger trigger = new KeywordTrigger(keywords);
            trigger.setHandle(new PrinterHelper.PrinterKeywordTriggerHandle());
            trigger.setSource(text);
            trigger.parse();
        } else {
            try {
                PrinterInterface.open();
                PrinterInterface.begin();
                printerWrite(PrinterCommand.init());
                printerWrite(PrinterCommand.setHeatTime(180));
                KeywordTrigger trigger = new KeywordTrigger(keywords);
                trigger.setHandle(new PrinterHelper.PrinterKeywordTriggerHandle());
                trigger.setSource(text);
                trigger.parse();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                PrinterInterface.end();
                PrinterInterface.close();
            }
        }
    }

    static class PrinterKeywordTriggerHandle extends KeywordTrigger.KeywordTriggerHandle {
        private boolean isBoldFont;
        private AlignEnum align;
        private int count;

        public PrinterKeywordTriggerHandle() {
        }

        public boolean getBoldFont() {
            return isBoldFont;
        }

        public AlignEnum getAlign() {
            return align;
        }

        @Override
        public void contentTrigger(String str) {
            if (DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_N3_OR_N5) {
                printerN3N5.appendPrnStr(str, FONT_SIZE_NORMAL, getAlign(), getBoldFont());
            } else {
                printText(str);
            }
        }

        @Override
        public void keywordTrigger(String keyword) {
            if (DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_N3_OR_N5) {
                if (keyword.equals("<b>")) {
                    isBoldFont = true;
                } else if (keyword.equals("</b>")) {
                    isBoldFont = false;
                } else if (keyword.equals("<c>")) {
                    align = AlignEnum.CENTER;
                } else if (keyword.equals("</c>")) {
                    align = AlignEnum.LEFT;
                } else if (keyword.equals("<w>")) {
                } else if (keyword.equals("</w>")) {
                } else if (keyword.equals("<h>")) {
                } else if (keyword.equals("</h>")) {
                } else if (keyword.equals("<s>")) {
                } else if (keyword.equals("</s>")) {
                } else if (keyword.equals("<i>")) {

                } else if (keyword.equals("</i>")) {

                } else if (keyword.equals("<l>")) {
                    align = AlignEnum.LEFT;
                } else if (keyword.equals("</l>")) {

                } else if (keyword.equals("<r>")) {
                    align = AlignEnum.RIGHT;
                } else if (keyword.equals("</r>")) {
                    align = AlignEnum.LEFT;
                } else if (keyword.equals("<bc>")) {

                } else if (keyword.equals("</bc>")) { // 一维码

                } else if (keyword.equals("<ul>")) {

                } else if (keyword.equals("</ul>")) { // 下划线

                } else if (keyword.equals("<img>")) {

                } else if (keyword.equals("</img>")) {

                } else if (keyword.equals("<nbr/>")) {
                    printerN3N5.appendPrnStr("\n", FONT_SIZE_NORMAL, AlignEnum.LEFT, false);
                } else if (keyword.equals("<t/>")) {

                } else if (keyword.equals("<sls>")) {
//                    write(PrinterCommand.getCmdEsc3N(20));
                } else if (keyword.equals("</sls>")) {
//                    write(PrinterCommand.getCmdEsc2());
                } else if (keyword.equals("<end/>")) {
                    printerN3N5.startPrint(true, new OnPrintListener() {
                        @Override
                        public void onPrintResult(final int retCode) {
                            Log.d("PrintHelp","N3/N5打印完成状态 = " + retCode);
                        }
                    });

                }
            } else {
                if (keyword.equals("<b>")) {
                    write(PrinterCommand.setFontBold(1));
                } else if (keyword.equals("</b>")) {
                    write(PrinterCommand.setFontBold(0));
                } else if (keyword.equals("<c>")) {
                    write(PrinterCommand.setAlignMode(1));
                } else if (keyword.equals("</c>")) {
                    write(PrinterCommand.linefeed());
                    write(PrinterCommand.setAlignMode(0));
                } else if (keyword.equals("<w>")) {
                    write(PrinterCommand.getCmdEscSo());

                } else if (keyword.equals("</w>")) {
                    write(PrinterCommand.getCmdEscDc4());
                } else if (keyword.equals("<h>")) {
                    write(PrinterCommand.setFontEnlarge(0x01));
                } else if (keyword.equals("</h>")) {
                    write(PrinterCommand.setFontEnlarge(0x00));
                } else if (keyword.equals("<s>")) {
                    write(PrinterCommand.getCmdSmallFontCN(1));
                    write(PrinterCommand.getCmdSmallFontEN(1));
                } else if (keyword.equals("</s>")) {
                    write(PrinterCommand.getCmdSmallFontCN(0));
                    write(PrinterCommand.getCmdSmallFontEN(0));
                } else if (keyword.equals("<i>")) {

                } else if (keyword.equals("</i>")) {

                } else if (keyword.equals("<l>")) {
                    write(PrinterCommand.setAlignMode(0));
                } else if (keyword.equals("</l>")) {

                } else if (keyword.equals("<r>")) {
                    write(PrinterCommand.setAlignMode(50));
                } else if (keyword.equals("</r>")) {
                    write(PrinterCommand.linefeed());
                    write(PrinterCommand.setAlignMode(2));
                } else if (keyword.equals("<bc>")) {

                } else if (keyword.equals("</bc>")) { // 一维码

                } else if (keyword.equals("<ul>")) {

                } else if (keyword.equals("</ul>")) { // 下划线

                } else if (keyword.equals("<img>")) {

                } else if (keyword.equals("</img>")) {

                } else if (keyword.equals("<br/>")) {
                    write(PrinterCommand.linefeed());
                } else if (keyword.equals("<t/>")) {

                } else if (keyword.equals("<sls>")) {
                    write(PrinterCommand.getCmdEsc3N(20));
                } else if (keyword.equals("</sls>")) {
                    write(PrinterCommand.getCmdEsc2());
                }
            }
        }
    }

    private static void write(byte[] data) {
        PrinterInterface.write(data, data.length);
    }

    private static void printText(String text) {
        try {
            byte[] bytes = text.getBytes("GB2312");
            PrinterInterface.write(bytes, bytes.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void printerWrite(byte[] data) {
        PrinterInterface.write(data, data.length);
    }

}
