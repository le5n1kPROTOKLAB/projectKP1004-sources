import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

@SuppressWarnings("ResultOfMethodCallIgnored, unused")
public class FileToBytesShow {
    private static final String GOAL_DIR_PATH = "C:\\FileToBytes";
    private static final String GOAL_FILE_NAME_STR = "splash";
    private static final String GOAL_FILE_NAME = GOAL_FILE_NAME_STR + ".img";
    private static final String TXT_FILE_NAME = GOAL_FILE_NAME_STR + ".txt";
    private static final String PNG_FILE_NAME = GOAL_FILE_NAME_STR + ".png";
    private static final String PNG_SAVE_FILE_NAME = GOAL_FILE_NAME_STR + "_save.png";
    private static final String JPG_FILE_NAME = GOAL_FILE_NAME_STR + ".jpg";
    private static final Color bgColor = new Color(0, 0, 0);

    public static void main(String[] args) {
//        saveImage();
        // PngToImg(1920, 720);
        PngToImg(1280, 800);
//        ImgToTxt();
//        TxtToImg();
//        calcLineAver();
    }

    private static void saveImage() {
        BufferedImage bufferImage;
        try {
            bufferImage = ImageIO.read(new File(GOAL_DIR_PATH, JPG_FILE_NAME));
            int width = bufferImage.getWidth();
            int height = bufferImage.getHeight();
            System.out.printf("width: %d, height: %d%n", width, height);
            for (int hIndex = 0; hIndex < 60; hIndex++) {
                // 一行
                int[] rgbArray = bufferImage.getRGB(0, hIndex, width, 1, new int[width], 0, 1);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < rgbArray.length; i++) {
                    Color color = new Color(rgbArray[i]);
                    stringBuilder.append(String.format("%02X ", color.getRed()))
                            .append(String.format("%02X ", color.getGreen()))
                            .append(String.format("%02X", color.getBlue()));
                    if (i<rgbArray.length-1){
                        stringBuilder.append(" ");
                    }
                }
                System.out.println("========== " + stringBuilder);
                System.out.println();
            }
        } catch (IOException e) {

        }




//        int width = 800;
//        int height = 600;
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        // 创建对象
//        Graphics2D graphics = image.createGraphics();
//        graphics.setColor(Color.RED);
//        graphics.fillRect(0, 0, width, height);
//
//        // 释放
//        graphics.dispose();
//
//        // 保存文件
//        File pngFile = new File(GOAL_DIR_PATH, PNG_SAVE_FILE_NAME);
//        try {
//            ImageIO.write(image, "png", pngFile);
//        } catch (IOException e) {
//            //noinspection CallToPrintStackTrace
//            e.printStackTrace();
//        }
        System.out.println("======================= 完成");
    }

    static class LineSpan {
        public static int TYPE_DIFF = 0;
        public static int TYPE_SAME = 1;
        int type;
        int count;
    }

    static class LineColorSpan {
        public boolean isSame;
        int count;
        ArrayList<Color> colors = new ArrayList<>();

        public byte[] toBytes() {
            int size = colors.size();
            int countValue;
            if (isSame) {
                countValue = 0x80 + count - 1;
            } else {
                countValue = count - 1;
            }
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byteStream.write(0xFF & countValue);
            for (int i = 0; i < size; i++) {
                Color color = colors.get(i);
                int rgb = color.getRGB();
                int R = color.getRed();
                int G = color.getGreen();
                int B = color.getBlue();

                byteStream.write(0xFF & B);
                byteStream.write(0xFF & G);
                byteStream.write(0xFF & R);
            }
            return byteStream.toByteArray();
        }

        @Override
        public String toString() {
            return getValueStr(toBytes());
        }
    }

    @SuppressWarnings("DefaultLocale")
    private static void PngToImg(int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        int HEADER_SIZE = 512;

        int bgRGB = bgColor.getRGB();
        BufferedOutputStream outputStream = null;
        try {
            BufferedImage bufferImage = ImageIO.read(new File(GOAL_DIR_PATH, PNG_FILE_NAME));
            int width = bufferImage.getWidth();
            int height = bufferImage.getHeight();
            System.out.printf("width: %d, height: %d%n", width, height);
            if (width > SCREEN_WIDTH || height > SCREEN_HEIGHT) {
                System.out.println("图片宽或高超过显示的区域宽高");
                return;
            }

            outputStream = new BufferedOutputStream(Files.newOutputStream(new File(GOAL_DIR_PATH, GOAL_FILE_NAME).toPath()));
            int[] lineRgbArray = new int[SCREEN_WIDTH];
            int[] result = new int[SCREEN_WIDTH - 1];
            int start = (SCREEN_WIDTH - width) / 2;
            int hStart = (SCREEN_HEIGHT - height) / 2;

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            for (int hIndex = 0; hIndex < SCREEN_HEIGHT; hIndex++) {
                // 一行
                // 填充背景色
                Arrays.fill(lineRgbArray, bgRGB);
                // 使用图片填充背景
                if (hIndex >= hStart && hIndex < hStart + height) {
                    int imageStart = hIndex - hStart;
                    int[] rgbArray = bufferImage.getRGB(0, imageStart, width, 1, new int[width], 0, 1);
                    System.out.println("rgb " + rgbArray.length);
                    System.arraycopy(rgbArray, 0, lineRgbArray, start, width);
                }
                // 对一行数据进行处理
                int lastRgb = 0;
                for (int wIndex = 0; wIndex < lineRgbArray.length; wIndex++) {
                    int rgb = lineRgbArray[wIndex];
                    if (wIndex > 0) {
                        result[wIndex - 1] = lastRgb == rgb ? LineSpan.TYPE_SAME : LineSpan.TYPE_DIFF;
                    }
                    lastRgb = rgb;
                }
                // 统计相同和不同值的间隔值
                ArrayList<LineSpan> lineSpans = new ArrayList<>();
                LineSpan curr = null;
                int lastType = -1;
                for (int i = 0; i < result.length; i++) {
                    int newType = result[i];
                    if (i > 0) {
                        if (newType == lastType) {
                            curr.count++;
                        } else {
                            curr = new LineSpan();
                            curr.type = newType;
                            curr.count++;
                            lineSpans.add(curr);
                        }
                    } else {
                        curr = new LineSpan();
                        curr.type = newType;
                        curr.count++;
                        lineSpans.add(curr);
                    }
                    lastType = newType;
                }
                // a bbbb ...ccc d 这种情况第一个和最后一个均会少一个0的计数，解决办法是在前后如果是0，对0的个数加1
                LineSpan firstLineSpan = lineSpans.get(0);
                if (firstLineSpan.type == LineSpan.TYPE_DIFF) {
                    firstLineSpan.count += 1;
                }
                LineSpan endLineSpan = lineSpans.get(lineSpans.size() - 1);
                if (endLineSpan.type == LineSpan.TYPE_DIFF) {
                    firstLineSpan.count += 1;
                }

                // 日志
                StringBuilder lineColorSpanStr = new StringBuilder();
                lineColorSpanStr.append(String.format("%04d", hIndex));
                for (int i = 0; i < lineSpans.size(); i++) {
                    LineSpan lineSpan = lineSpans.get(i);
                    int type = lineSpan.type;
                    int count;
                    if (type == LineSpan.TYPE_SAME) {
                        count = lineSpan.count + 1;
                        lineColorSpanStr.append(" ").append(type).append("(").append(count).append(")");
                    } else {
                        if (lineSpan.count > 1) {
                            count = lineSpan.count - 1;
                            lineColorSpanStr.append(" ").append(type).append("(").append(count).append(")");
                        }
                    }
                }
                System.out.println(lineColorSpanStr);
                // 通过间隔值可等到每个区域的起始位置和长度，再去行数据取颜色值
                // 相同区域的长度为 统计值+1，不同区域的长度为 统计值-1

                ArrayList<LineColorSpan> lineColorSpans = new ArrayList<>();
                int currIndex = 0;
                for (LineSpan lineSpan : lineSpans) {
                    int length;
                    if (lineSpan.type == LineSpan.TYPE_SAME) {
                        length = lineSpan.count + 1;
                        // length 需要判断是否超过 0x80, 超过需要分段
                        int size = 0;
                        for (int i = 0; i < length; i++) {
                            size++;
                            if (size >= 0x80 || i == length - 1) {
                                LineColorSpan lineColorSpan = new LineColorSpan();
                                lineColorSpan.count = size;
                                lineColorSpan.isSame = true;
                                lineColorSpan.colors.add(new Color(lineRgbArray[currIndex]));
                                lineColorSpans.add(lineColorSpan);
                                currIndex += size;
                                size = 0;
                            }
                        }
                    } else {
                        length = lineSpan.count - 1;
                        if (length > 0) {
                            // length 需要判断是否超过 0x80, 超过需要分段
                            int size = 0;
                            for (int i = 0; i < length; i++) {
                                size++;
                                if (size >= 0x80 || i == length - 1) {
                                    LineColorSpan lineColorSpan = new LineColorSpan();
                                    lineColorSpan.count = size;
                                    lineColorSpan.isSame = false;
                                    for (int j = 0; j < size; j++) {
                                        int rgb = lineRgbArray[currIndex + j];
                                        lineColorSpan.colors.add(new Color(rgb));
                                    }
                                    lineColorSpans.add(lineColorSpan);
                                    currIndex += size;
                                    size = 0;
                                }
                            }
                        }
                    }
                }

                System.out.println("======================= " + String.format("%04d", hIndex));
                StringBuilder lineStr = new StringBuilder();
                for (int i = 0; i < lineColorSpans.size(); i++) {
                    LineColorSpan lineColorSpan = lineColorSpans.get(i);
                    byte[] bytes = lineColorSpan.toBytes();
                    byteStream.write(bytes, 0, bytes.length);
                    lineStr.append(getValueStr(bytes));
                    if (i < lineColorSpans.size() - 1) {
                        lineStr.append(" ");
                    }
                }
                System.out.println(lineStr);
                System.out.println();
            }
            byteStream.flush();

            byte[] header = new byte[HEADER_SIZE];
            // SPLASH!!
            byte[] splashBytes = "SPLASH!!".getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(splashBytes, 0, header, 0, splashBytes.length);
            // width
            System.arraycopy(IntToBytes(SCREEN_WIDTH), 0, header, 8, 4);
            // height
            System.arraycopy(IntToBytes(SCREEN_HEIGHT), 0, header, 12, 4);
            // 512 区域个数起始
            System.arraycopy(IntToBytes(1), 0, header, 16, 4);

            byte[] byteArray = byteStream.toByteArray();
            int dataSize = byteArray.length;
            // 512 区域个数结尾
            System.arraycopy(IntToBytes(dataSize / HEADER_SIZE + 1), 0, header, 20, 4);

            outputStream.write(header, 0, header.length);
            outputStream.write(byteArray);
            outputStream.flush();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        System.out.println("======================= 完成 ");
    }

    /**
     * 使用一行数据
     * 用来测试解析算法是否正确
     */
    @SuppressWarnings("DefaultLocale")
    private static void calcLineAver() {
        String text = "FF 00 00 00 FF 00 00 00 FF 00 00 00 FF 00 00 00 DB 00 00 00 00 17 11 01 82 B9 98 19 00 60 4E 08 FF 00 00 00 FF 00 00 00 FF 00 00 00 FF 00 00 00 FF 00 00 00 9E 00 00 00";
        String[] split = text.split(" ");

        int sum = 0;
        boolean isLineStart = true;
        int startIndex = 0;
        int endIndex = 0;

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            int hexValue;
            int number;
            if (isLineStart) {
                isLineStart = false;
                hexValue = Integer.parseInt(s, 16);
                stringBuilder.append(String.format("X: %02X\r\n", hexValue));
                startIndex = i;
                if (hexValue < 0x80) {
                    // 非重复颜色点
                    endIndex = startIndex + 3 * (hexValue + 1);
                    number = hexValue + 1;
                } else {
                    endIndex = startIndex + 3;
                    number = hexValue + 1 - 0x80;
                }
                sum += number;
            } else {
                stringBuilder.append(s);
                if ((i - startIndex) % 3 == 0) {
                    if (i == endIndex) {
                        isLineStart = true;
                        stringBuilder.append("\r\n");
                    }
                    stringBuilder.append("\r\n");
                } else {
                    stringBuilder.append(" ");
                }
            }
        }
        System.out.println(stringBuilder);
        System.out.println("sum " + String.format("X: %X， D: %d", sum, sum));
        System.out.println("aver " + String.format("X: %02X,  D: %d", sum / 10 - 1, sum / 10 - 1));
    }

    /**
     * 按算法解析为十六进制行文本
     * 格式为 AA (BB CC DD) (BB CC DD)
     * AA为像素长度， 括号内为颜色
     * 以 1280 * 800 为例 一行按下列规则长度加起来为 1280
     * AA；1，>= 0x80 像素点颜色重复，实际长度为 AA + 1 - 0x80 (1~128)
     * 2，<  0x80 像素点颜色重复，实际长度为 AA + 1 (1~128)
     */
    private static void ImgToTxt() {
        File goalDir = new File(GOAL_DIR_PATH, GOAL_FILE_NAME);
        File goalOutDir = new File(GOAL_DIR_PATH, TXT_FILE_NAME);
        BufferedInputStream inputStream = null;
        BufferedWriter outputStream = null;
        try {
            if (!goalOutDir.exists()) {
                goalOutDir.createNewFile();
            }
            inputStream = new BufferedInputStream(Files.newInputStream(goalDir.toPath()));
            outputStream = new BufferedWriter(Files.newBufferedWriter(goalOutDir.toPath()));

            byte[] buffer = new byte[1024];
            long length;
            long num = 0;
            long totalLength = goalDir.length();
            byte[] valueBytes = new byte[4];
            long currentLength = 0;
            StringBuilder stringBuilder = null;
            long countPx = 0;

            boolean isColorStart = true;
            long startIndex = 0;
            long endIndex = 0;
            int hexValue;
            int LINE_UNIT = 0X80;
            byte[] widthBytes = new byte[4];
            long LINE_PX = 1028;
            byte[] heightBytes = new byte[4];
            long LINE_COUNT = LINE_PX / LINE_UNIT;
            long currLineCount = 0;
            int COLOR_UNIT = 3;
            int number;
            boolean isNeedSpace;

            while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                for (int i = 0; i < length; i++) {
                    if (stringBuilder == null) {
                        stringBuilder = new StringBuilder();
                    }
                    byte currentByte = buffer[i];
                    String currentByteStr = String.format("%02X", currentByte);
                    if (currentLength < 511) {
                        if (currentLength == 8) {
                            widthBytes[0] = currentByte;
                        } else if (currentLength == 9) {
                            widthBytes[1] = currentByte;
                        } else if (currentLength == 10) {
                            widthBytes[2] = currentByte;
                        } else if (currentLength == 11) {
                            widthBytes[3] = currentByte;
                        } else if (currentLength == 12) {
                            heightBytes[0] = currentByte;
                        } else if (currentLength == 13) {
                            heightBytes[1] = currentByte;
                        } else if (currentLength == 14) {
                            heightBytes[2] = currentByte;
                        } else if (currentLength == 15) {
                            heightBytes[3] = currentByte;
                        } else if (currentLength == 20) {
                            System.out.println(currentByteStr);
                            valueBytes[0] = currentByte;
                        } else if (currentLength == 21) {
                            valueBytes[1] = currentByte;
                        } else if (currentLength == 22) {
                            valueBytes[2] = currentByte;
                        } else if (currentLength == 23) {
                            valueBytes[3] = currentByte;
                        }
                        stringBuilder.append(currentByteStr);
                        stringBuilder.append(" ");
                    } else if (currentLength == 511) {
                        stringBuilder.append(currentByteStr);
                        System.out.println(stringBuilder);
                        LINE_PX = getValue(widthBytes);
                        LINE_COUNT = LINE_PX / LINE_UNIT;
                        outputStream.write(stringBuilder.toString());
                        outputStream.newLine();
                        stringBuilder = null;
                        num++;
                    } else {
                        // >512 4个字节为一组
                        isNeedSpace = true;
                        if (isColorStart) {
                            isColorStart = false;
                            hexValue = Integer.parseInt(currentByteStr, 16);
                            stringBuilder.append(currentByteStr);
                            startIndex = currentLength;
                            if (hexValue < LINE_UNIT) {
                                // 非重复颜色点
                                endIndex = startIndex + (long) COLOR_UNIT * (hexValue + 1);
                                number = hexValue + 1;
                            } else {
                                endIndex = startIndex + COLOR_UNIT;
                                number = hexValue + 1 - LINE_UNIT;
                            }
                            currLineCount += number;
                        } else {
                            stringBuilder.append(currentByteStr);
                            if ((currentLength - startIndex) % COLOR_UNIT == 0) {
                                if (currentLength == endIndex) {
                                    isColorStart = true;
                                    if (currLineCount >= LINE_PX) {
                                        currLineCount = 0;
                                        isNeedSpace = false;
                                        System.out.println(stringBuilder);
                                        outputStream.write(stringBuilder.toString());
                                        if (currentLength < totalLength - 1) {
                                            outputStream.newLine();
                                        }
                                        stringBuilder = null;
                                        num++;
                                    }
                                }
                            } else {
                                if (currentLength >= totalLength - 1) {
                                    isColorStart = true;
                                    currLineCount = 0;
                                    isNeedSpace = false;
                                    System.out.println(stringBuilder);
                                    outputStream.write(stringBuilder.toString());
                                    stringBuilder = null;
                                    num++;
                                }
                            }
                        }
                        if (isNeedSpace) {
                            stringBuilder.append(" ");
                        }
                    }
                    currentLength++;
                }
            }
            long dataSize = totalLength - 512;
            System.out.println("totalLength: " + totalLength);
            System.out.println("dataSize: " + dataSize);
            long value = getValue(valueBytes);
            System.out.println("value: " + value);

            System.out.println("dataSize/data: " + dataSize * 1f / value);
            System.out.println("dataSize/512: " + (dataSize / 512 + 1));
            System.out.printf("num: %d  X: %08X %n", num, num);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {

                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {

                }
            }
        }
        System.out.println("======================= 完成");
    }

    private static void TxtToImg() {
        File goalDir = new File(GOAL_DIR_PATH, TXT_FILE_NAME);
        File goalOutDir = new File(GOAL_DIR_PATH, GOAL_FILE_NAME);
        BufferedReader bufferedReader;
        BufferedOutputStream outputStream = null;
        try {
            if (!goalOutDir.exists()) {
                goalOutDir.createNewFile();
            }
            bufferedReader = new BufferedReader(Files.newBufferedReader(goalDir.toPath()));
            outputStream = new BufferedOutputStream(Files.newOutputStream(goalOutDir.toPath()));
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                String[] split = temp.split(" ");
                byte[] bytes = new byte[split.length];
                for (int i = 0; i < split.length; i++) {
                    bytes[i] = (byte) Integer.parseInt(split[i], 16);
                }
                outputStream.write(bytes, 0, bytes.length);
            }
            System.out.println("完成 ");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {

                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    private static long getValue(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value |= (long) (0xFF & bytes[i]) << (8 * i);
        }
        return value;
    }

    private static String getValueStr(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            stringBuilder.append(String.format("%02X", bytes[i]));
            if (i < bytes.length - 1) {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }

    private static byte[] IntToBytes(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (0xFF & (value >>> (8 * i)));
        }
        return bytes;
    }

    private static void testSum() {
        String text = "1(264) 1(3) 1(4) 1(9) 1(8) 1(4) 1(68) 1(2) 1(3) 1(3) 1(2) 1(2) 0(1) 1(3) 0(21) 1(2) 0(4) 1(2) 0(5) 1(2) 0(4) 1(14) 1(4) 1(6) 0(12) 1(2) 0(1) 1(2) 0(11) 1(2) 0(1) 1(2) 0(2) 1(3) 0(2) 1(9) 1(2) 1(2) 1(2) 0(22) 1(2) 0(14) 1(3) 1(2) 0(2) 1(2) 0(2) 1(2) 0(6) 1(2) 0(2) 1(2) 0(5) 1(2) 0(23) 1(2) 1(3) 0(21) 1(2) 1(4) 1(4) 0(1) 1(3) 1(2) 1(2) 0(3) 1(2) 0(28) 1(4) 1(2) 1(6) 1(2) 0(2) 1(3) 1(3) 1(3) 1(14) 0(4) 1(2) 0(20) 1(2) 0(12) 1(2) 0(30) 1(2) 0(2) 1(2) 0(26) 1(2) 0(35) 1(2) 0(2) 1(2) 0(2) 1(2) 0(26) 1(2) 0(24) 1(2) 0(11) 1(2) 0(1) 1(2) 0(5) 1(5) 1(3) 1(3) 1(2) 0(1) 1(2) 1(2) 0(3) 1(2) 1(3) 1(2) 1(2) 1(2) 1(6) 0(1) 1(2) 1(5) 0(14) 1(2) 0(4) 1(2) 1(2) 0(1) 1(3) 1(8) 1(3) 1(5) 0(1) 1(2) 1(4) 1(2) 0(2) 1(2) 1(2) 0(10) 1(3) 0(3) 1(2) 0(5) 1(2) 1(2) 0(8) 1(2) 0(2) 1(2) 0(1) 1(3) 0(1) 1(2) 0(12) 1(3) 0(1) 1(2) 1(3) 1(2) 1(3) 0(3) 1(4) 1(3) 0(3) 1(3) 1(3) 0(4) 1(3) 0(1) 1(6) 0(1) 1(4) 1(3) 0(2) 1(2) 1(2) 0(2) 1(2) 1(3) 0(7) 1(5) 1(2) 1(2) 1(3) 0(3) 1(4) 0(2) 1(2) 0(3) 1(2) 0(11) 1(2) 0(17) 1(3) 1(2) 0(13) 1(2) 0(3) 1(3) 0(4) 1(3) 1(2) 1(7) 1(2) 1(2) 1(2) 1(3)";
        String[] split = text.split(" ");
        int count = 0;
        for (String s : split) {
            count += Integer.parseInt(s.substring(s.indexOf('(') + 1, s.length() - 1));
        }
        System.out.println(count);
    }
}
