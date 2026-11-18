import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class TreeDrawer {

    public static void drawBinaryTree(BinomialNode root, int nodeCount, File output) throws IOException {
        int depth = binaryTreeDepth(root);
        int nodeSpacing = 42;
        int levelSpacing = 54;
        int margin = 32;
        int width = Math.max(300, nodeCount * nodeSpacing + margin * 2);
        int height = Math.max(180, depth * levelSpacing + margin * 2);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setStroke(new BasicStroke(2));

            Map<BinomialNode, int[]> positions = new IdentityHashMap<>();
            int[] nextPosition = {0};
            assignBinaryPositions(root, 0, nextPosition, positions, nodeSpacing, levelSpacing, margin);
            drawBinaryEdges(root, positions, graphics);
            drawBinaryNodes(root, positions, graphics);
        } finally {
            graphics.dispose();
        }

        saveImage(image, output);
    }

    public static void drawBinomialTree(BinomialNode root, File output) throws IOException {
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 15);
        BufferedImage measurement = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D measureGraphics = measurement.createGraphics();
        int diameter;
        try {
            measureGraphics.setFont(font);
            diameter = Math.max(42, maxBinomialLabelWidth(root, measureGraphics) + 16);
        } finally {
            measureGraphics.dispose();
        }

        int margin = 32;
        int nodeSpacing = diameter + 20;
        int levelSpacing = diameter + 24;
        Map<BinomialNode, int[]> positions = new IdentityHashMap<>();
        int[] leafCount = {0};
        int[] maxLevel = {0};
        assignBinomialPositions(root, 0, leafCount, maxLevel, positions,
                nodeSpacing, levelSpacing, margin, diameter);
        int width = Math.max(300, margin * 2 + Math.max(1, leafCount[0]) * nodeSpacing);
        int height = Math.max(180, margin * 2 + diameter + maxLevel[0] * levelSpacing);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setStroke(new BasicStroke(2));
            graphics.setFont(font);
            drawBinomialEdges(root, positions, graphics);
            drawBinomialNodes(root, positions, graphics, diameter);
        } finally {
            graphics.dispose();
        }
        saveImage(image, output);
    }

    private static int maxBinomialLabelWidth(BinomialNode node, Graphics2D graphics) {
        if (node == null) {
            return 0;
        }
        int width = graphics.getFontMetrics().stringWidth(Integer.toString(node.getVal()));
        for (BinomialNode child = node.getChild(); child != null; child = child.getNext()) {
            width = Math.max(width, maxBinomialLabelWidth(child, graphics));
        }
        return width;
    }

    private static int assignBinomialPositions(
            BinomialNode node, int level, int[] leafCount, int[] maxLevel,
            Map<BinomialNode, int[]> positions, int nodeSpacing, int levelSpacing,
            int margin, int diameter
    ) {
        if (node == null) {
            return 0;
        }
        maxLevel[0] = Math.max(maxLevel[0], level);
        int x;
        if (node.getChild() == null) {
            x = margin + diameter / 2 + leafCount[0]++ * nodeSpacing;
        } else {
            int firstX = 0;
            int lastX = 0;
            boolean first = true;
            for (BinomialNode child = node.getChild(); child != null; child = child.getNext()) {
                lastX = assignBinomialPositions(child, level + 1, leafCount, maxLevel,
                        positions, nodeSpacing, levelSpacing, margin, diameter);
                if (first) {
                    firstX = lastX;
                    first = false;
                }
            }
            x = firstX + (lastX - firstX) / 2;
        }
        positions.put(node, new int[]{x, margin + diameter / 2 + level * levelSpacing});
        return x;
    }

    private static void drawBinomialEdges(
            BinomialNode node, Map<BinomialNode, int[]> positions, Graphics2D graphics
    ) {
        if (node == null) {
            return;
        }
        int[] parent = positions.get(node);
        graphics.setColor(new Color(90, 100, 115));
        for (BinomialNode child = node.getChild(); child != null; child = child.getNext()) {
            int[] childPosition = positions.get(child);
            graphics.drawLine(parent[0], parent[1], childPosition[0], childPosition[1]);
            drawBinomialEdges(child, positions, graphics);
        }
    }

    private static void drawBinomialNodes(
            BinomialNode node, Map<BinomialNode, int[]> positions, Graphics2D graphics, int diameter
    ) {
        if (node == null) {
            return;
        }
        drawValue(node.getVal(), positions.get(node), graphics, diameter);
        for (BinomialNode child = node.getChild(); child != null; child = child.getNext()) {
            drawBinomialNodes(child, positions, graphics, diameter);
        }
    }

    private static void saveImage(BufferedImage image, File output) throws IOException {
        if (!ImageIO.write(image, "png", output)) {
            throw new IOException("Nie udalo sie zapisac obrazu PNG.");
        }
        System.out.println("Zapisano wizualizacje drzewa: " + output.getAbsolutePath());
    }

    private static int binaryTreeDepth(BinomialNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(binaryTreeDepth(node.getPrev()), binaryTreeDepth(node.getNext()));
    }

    private static void assignBinaryPositions(
            BinomialNode node,
            int level,
            int[] nextPosition,
            Map<BinomialNode, int[]> positions,
            int nodeSpacing,
            int levelSpacing,
            int margin
    ) {
        if (node == null) {
            return;
        }
        assignBinaryPositions(node.getPrev(), level + 1, nextPosition, positions,
                nodeSpacing, levelSpacing, margin);
        int x = margin + nextPosition[0] * nodeSpacing + nodeSpacing / 2;
        int y = margin + level * levelSpacing + levelSpacing / 2;
        positions.put(node, new int[]{x, y});
        nextPosition[0]++;
        assignBinaryPositions(node.getNext(), level + 1, nextPosition, positions,
                nodeSpacing, levelSpacing, margin);
    }

    private static void drawBinaryEdges(
            BinomialNode node, Map<BinomialNode, int[]> positions, Graphics2D graphics
    ) {
        if (node == null) {
            return;
        }
        int[] parent = positions.get(node);
        graphics.setColor(new Color(90, 100, 115));
        if (node.getPrev() != null) {
            int[] leftPosition = positions.get(node.getPrev());
            graphics.drawLine(parent[0], parent[1], leftPosition[0], leftPosition[1]);
        }
        if (node.getNext() != null) {
            int[] rightPosition = positions.get(node.getNext());
            graphics.drawLine(parent[0], parent[1], rightPosition[0], rightPosition[1]);
        }
        drawBinaryEdges(node.getPrev(), positions, graphics);
        drawBinaryEdges(node.getNext(), positions, graphics);
    }

    private static void drawBinaryNodes(
            BinomialNode node, Map<BinomialNode, int[]> positions, Graphics2D graphics
    ) {
        if (node == null) {
            return;
        }
        drawValue(node.getVal(), positions.get(node), graphics, 42);
        drawBinaryNodes(node.getPrev(), positions, graphics);
        drawBinaryNodes(node.getNext(), positions, graphics);
    }

    private static void drawValue(int value, int[] position, Graphics2D graphics, int diameter) {
        graphics.setColor(new Color(65, 125, 210));
        graphics.fillOval(position[0] - diameter / 2, position[1] - diameter / 2, diameter, diameter);
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        String text = Integer.toString(value);
        int textWidth = graphics.getFontMetrics().stringWidth(text);
        graphics.drawString(text, position[0] - textWidth / 2, position[1] + 5);
    }
}
