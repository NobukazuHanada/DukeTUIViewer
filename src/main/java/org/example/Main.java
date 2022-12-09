package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws IOException {
        var size = 70;
        if (args.length >= 1) {
            try {
                size = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Numerical input could not be verified.");
            }
        }
        new Main().drawDuke(size);
    }

    public double grayScale(int rgb) {
        var color = new Color(rgb);
        return color.getRed() * 0.3 + color.getGreen() * 0.59 + color.getBlue() * 0.11;
    }

    public void drawDuke(int size) throws IOException {
        var classLoader = getClass().getClassLoader();
        var dukeImage = ImageIO.read(Objects.requireNonNull(classLoader.getResource("duke/Wave.png")));
        var imageWidth = dukeImage.getWidth();
        var imageHeight = dukeImage.getHeight();
        // Number of horizontal rows of squares to divide the image
        // width of squares to divide the image
        int splitSquareWidth = imageWidth / size;
        // Number of squares to divide the image
        var splitSquaresCount = size * (imageHeight / splitSquareWidth);
        // Number of pixels in the square to be divided
        int splitSquarePixelsCount = splitSquareWidth * splitSquareWidth;
        // Threshold for outputting # or blanks above the grayscale mean of the squares
        var threshold = 122;

        // Find the average of the gray scale of the squares into which the image is divided
        var grayScales = IntStream.range(0, splitSquaresCount).mapToDouble(indexSquare -> {
            int squareX = (indexSquare % size) * splitSquareWidth;
            int squareY = (indexSquare / size) * splitSquareWidth;

            // Find the gray scale average of a square
            return IntStream.range(0, splitSquarePixelsCount).mapToDouble(indexPixelsOfSquare -> {
                int pixelX = indexPixelsOfSquare % splitSquareWidth;
                int pixelY = indexPixelsOfSquare / splitSquareWidth;
                return grayScale(dukeImage.getRGB(squareX + pixelX, squareY + pixelY));
            }).average().orElseThrow();
        }).toArray();

        // Square grayscale average to # or blank character
        // and add LineSeparator to the text
        var sp = System.lineSeparator();
        var dukeText = IntStream.range(0, grayScales.length).mapToObj(index -> {
            var grayScale = grayScales[index];
            // Condition for the line is the last
            var isLast = (index % size) == (size - 1);
            if (grayScale < threshold) {
                return " " + (isLast ? sp : "");
            } else {
                return "#" + (isLast ? sp : "");
            }
        }).collect(Collectors.joining());

        System.out.println(dukeText);
    }
}