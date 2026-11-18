import java.io.File;
import java.io.IOException;
import java.util.Random;

void main() throws IOException {
    Random random = new Random();

    int binomialHeight = 5;

    //------------------------------------------------
    // Zad 1.
    // Znajdowanie najgłębszych węzłów drzewa binarnego
    //------------------------------------------------
    BinomialNode binaryTree = createRandomBinomialTree(1 << binomialHeight, 0, random);
    TreeDrawer.drawBinaryTree(binaryTree, 1 << binomialHeight, new File("binaryTreeForBinomial.png"));

    System.out.println("Największa głębokość drzewa binarnego: ???");
    System.out.println("Najgłębsze węzły drzewa binarnego:");
    System.out.println();

    //------------------------------------------------
    // Zad 2.
    // Konwersja drzewa binarnego do drzewa dwumianowego
    //------------------------------------------------
    BinomialNode convertedBinomialTree = null;

    TreeDrawer.drawBinomialTree(convertedBinomialTree, new File("binaryToBinomialTree.png"));
}

//--------------------------------------------------------------------------------------------------
//Funkcje pomocnicze :
//--------------------------------------------------------------------------------------------------
private BinomialNode createRandomBinomialTree(int size, int depth, Random random) {
    if (size == 0) {
        return null;
    }

    int leftSize;
    if (size == 1) {
        leftSize = 0;
    } else if (size == 2) {
        leftSize = random.nextInt(2);
    } else {
        double oneChildProbability = Math.min(0.5, depth * 0.05);
        if (random.nextDouble() < oneChildProbability) {
            leftSize = random.nextBoolean() ? 0 : size - 1;
        } else {
            leftSize = 1 + random.nextInt(size - 2);
        }
    }
    int rightSize = size - leftSize - 1;
    return new BinomialNode(
            random.nextInt(101),
            createRandomBinomialTree(leftSize, depth + 1, random),
            createRandomBinomialTree(rightSize, depth + 1, random)
    );
}
