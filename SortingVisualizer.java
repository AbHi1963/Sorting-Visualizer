import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class SortingVisualizer extends JPanel {
    private static final int SCREEN_WIDTH = 910;
    private static final int SCREEN_HEIGHT = 750;
    private static final int ARR_SIZE = 130;
    private static final int RECT_SIZE = 7;
    private int[] arr = new int[ARR_SIZE];
    private int[] barr = new int[ARR_SIZE];
    private boolean complete = false;
    private String sortType = "";
    private JSlider speedSlider;
    private JButton pauseButton;
    private boolean isPaused = false;
    private final Object pauseLock = new Object();
    private JLabel speedLabel;
    private JLabel timeLabel;

    public SortingVisualizer() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.randomizeAndSaveArray();
        this.setLayout(new FlowLayout());

        JButton selectionSortButton = new JButton("Selection Sort");
        JButton insertionSortButton = new JButton("Insertion Sort");
        JButton bubbleSortButton = new JButton("Bubble Sort");
        JButton mergeSortButton = new JButton("Merge Sort");
        JButton quickSortButton = new JButton("Quick Sort");
        JButton heapSortButton = new JButton("Heap Sort");

        final JLabel sortTypeLabel = new JLabel("Sort Type: ");
        sortTypeLabel.setForeground(Color.CYAN);

        speedLabel = new JLabel("Speed: 100");
        speedLabel.setForeground(Color.WHITE);

        timeLabel = new JLabel("Time: 0.0s");
        timeLabel.setForeground(Color.RED);

        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 100);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> speedLabel.setText("Speed: " + speedSlider.getValue()));

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> {
            if (isPaused) {
                pauseButton.setText("Pause");
                resumeSorting();
            } else {
                pauseButton.setText("Resume");
                pauseSorting();
            }
        });

        selectionSortButton.addActionListener(e -> startSorting("Selection Sort", sortTypeLabel));
        insertionSortButton.addActionListener(e -> startSorting("Insertion Sort", sortTypeLabel));
        bubbleSortButton.addActionListener(e -> startSorting("Bubble Sort", sortTypeLabel));
        mergeSortButton.addActionListener(e -> startSorting("Merge Sort", sortTypeLabel));
        quickSortButton.addActionListener(e -> startSorting("Quick Sort", sortTypeLabel));
        heapSortButton.addActionListener(e -> startSorting("Heap Sort", sortTypeLabel));

        this.add(selectionSortButton);
        this.add(insertionSortButton);
        this.add(bubbleSortButton);
        this.add(mergeSortButton);
        this.add(quickSortButton);
        this.add(heapSortButton);
        this.add(sortTypeLabel);
        this.add(speedLabel);
        this.add(timeLabel);
        this.add(new JLabel("Speed: "));
        this.add(speedSlider);
        this.add(pauseButton);
        this.setFocusable(true);

        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_1 -> startSorting("Selection Sort", sortTypeLabel);
                    case KeyEvent.VK_2 -> startSorting("Insertion Sort", sortTypeLabel);
                    case KeyEvent.VK_3 -> startSorting("Bubble Sort", sortTypeLabel);
                    case KeyEvent.VK_4 -> startSorting("Merge Sort", sortTypeLabel);
                    case KeyEvent.VK_5 -> startSorting("Quick Sort", sortTypeLabel);
                    case KeyEvent.VK_6 -> startSorting("Heap Sort", sortTypeLabel);
                }
            }
        });
    }

    private void startSorting(String algorithm, JLabel label) {
        loadArr();
        complete = false;
        sortType = algorithm;
        label.setText("Sort Type: " + sortType);
        timeLabel.setText("Time: 0.0s");

        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            switch (algorithm) {
                case "Selection Sort" -> selectionSort();
                case "Insertion Sort" -> insertionSort();
                case "Bubble Sort" -> bubbleSort();
                case "Merge Sort" -> mergeSort(arr, 0, ARR_SIZE - 1);
                case "Quick Sort" -> quickSort(arr, 0, ARR_SIZE - 1);
                case "Heap Sort" -> inplaceHeapSort(arr, ARR_SIZE);
            }
            complete = true;
            long endTime = System.currentTimeMillis();
            double timeTaken = (endTime - startTime) / 1000.0;
            timeLabel.setText(String.format("Time: %.2fs", timeTaken));
            repaint();
        }).start();
    }

    private void randomizeAndSaveArray() {
        Random random = new Random();
        for (int i = 0; i < ARR_SIZE; i++) {
            barr[i] = random.nextInt(SCREEN_HEIGHT);
        }
    }

    private void loadArr() {
        System.arraycopy(barr, 0, arr, 0, ARR_SIZE);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < ARR_SIZE; i++) {
            if (complete) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.GRAY);
            }
            g.fillRect(i * RECT_SIZE, SCREEN_HEIGHT - arr[i], RECT_SIZE, arr[i]);
        }
    }

    private void visualize(int x, int y) {
        repaint();
        try {
            synchronized (pauseLock) {
                while (isPaused) {
                    pauseLock.wait();
                }
            }
            Thread.sleep(101 - speedSlider.getValue());
        } catch (InterruptedException ignored) {
        }
    }

    private void pauseSorting() {
        isPaused = true;
    }

    private void resumeSorting() {
        isPaused = false;
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    private void selectionSort() {
        for (int i = 0; i < ARR_SIZE - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < ARR_SIZE; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
                visualize(i, minIndex);
            }
            int temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
            repaint();
        }
        complete = true;
    }

    private void insertionSort() {
        for (int i = 1; i < ARR_SIZE; i++) {
            int j = i - 1;
            int temp = arr[i];
            while (j >= 0 && arr[j] > temp) {
                arr[j + 1] = arr[j];
                j--;
                visualize(i, j + 1);
            }
            arr[j + 1] = temp;
            repaint();
        }
        complete = true;
    }

    private void bubbleSort() {
        for (int i = 0; i < ARR_SIZE - 1; i++) {
            for (int j = 0; j < ARR_SIZE - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    visualize(j, j + 1);
                }
            }
            repaint();
        }
        complete = true;
    }

    private void mergeSort(int[] a, int si, int ei) {
        if (si < ei) {
            int mid = (si + ei) / 2;
            mergeSort(a, si, mid);
            mergeSort(a, mid + 1, ei);
            merge2SortedArrays(a, si, ei);
            repaint();
        }
    }

    private void merge2SortedArrays(int[] a, int si, int ei) {
        int size_output = ei - si + 1;
        int[] output = new int[size_output];
        int mid = (si + ei) / 2;
        int i = si, j = mid + 1, k = 0;

        while (i <= mid && j <= ei) {
            if (a[i] <= a[j]) {
                output[k++] = a[i++];
            } else {
                output[k++] = a[j++];
            }
            visualize(i, j);
        }

        while (i <= mid) {
            output[k++] = a[i++];
            visualize(i, -1);
        }

        while (j <= ei) {
            output[k++] = a[j++];
            visualize(j, -1);
        }

        System.arraycopy(output, 0, a, si, size_output);
        visualize(-1, -1);
    }

    private void quickSort(int[] a, int si, int ei) {
        if (si < ei) {
            int c = partitionArray(a, si, ei);
            quickSort(a, si, c - 1);
            quickSort(a, c + 1, ei);
            repaint();
        }
    }

    private int partitionArray(int[] a, int si, int ei) {
        int pivot = a[si];
        int count = 0;
        for (int i = si + 1; i <= ei; i++) {
            if (a[i] <= pivot) {
                count++;
            }
        }
        int pivotIndex = si + count;
        int temp = a[pivotIndex];
        a[pivotIndex] = a[si];
        a[si] = temp;

        int i = si, j = ei;
        while (i < pivotIndex && j > pivotIndex) {
            while (a[i] <= pivot) {
                i++;
            }
            while (a[j] > pivot) {
                j--;
            }
            if (i < pivotIndex && j > pivotIndex) {
                temp = a[i];
                a[i] = a[j];
                a[j] = temp;
                visualize(i, j);
            }
        }
        return pivotIndex;
    }

    private void inplaceHeapSort(int[] arr, int n) {
        for (int i = n / 2 - 1; i >= 0; i--) {
            downHeapify(arr, n, i);
        }
        for (int i = n - 1; i >= 0; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            visualize(0, i);
            downHeapify(arr, i, 0);
        }
        complete = true;
    }

    private void downHeapify(int[] arr, int n, int i) {
        int largest = i;
        int leftChild = 2 * i + 1;
        int rightChild = 2 * i + 2;
        if (leftChild < n && arr[leftChild] > arr[largest]) {
            largest = leftChild;
        }
        if (rightChild < n && arr[rightChild] > arr[largest]) {
            largest = rightChild;
        }
        if (largest != i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;
            visualize(i, largest);
            downHeapify(arr, n, largest);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sorting Visualizer");
        SortingVisualizer visualizer = new SortingVisualizer();
        frame.add(visualizer);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
