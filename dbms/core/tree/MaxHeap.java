package core.tree;

import java.util.Random;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/27/2015
 */
public class MaxHeap<T> {
    // build maximum heap (max in root)
    public void heapify(T[] data) {
        int size = data.length;
        for (int i = size / 2; i >= 0; i--) {
            siftDown(data, i, size);
        }
    }



    // non-recursive siftDown procedure.
    public void siftDown(T[] data, int i, int size) {
        while (2 * i + 2 < size) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            Comparable cleft = (Comparable) data[left];
            Comparable cright = (Comparable) data[right];

            Comparable ct = cleft;
            int t = left;
            // choose bigger element between left child and right child
            if (right < size && cleft.compareTo(cright) < 0) {
                ct = cright;
                t = right;
            }

            T vali = data[i];
            cleft = (Comparable) vali;
            cright = ct;
            if (cleft.compareTo(cright) < 0) {
                // swap parent with left or right child
                data[i] = data[t];
                data[t] = vali;

                i = t;
            } else {
                break;
            }
        }
    }

    public static void main(String[] args) {
        Long[] times = new Long[10];
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            times[i] = rand.nextLong() % 100;
        }

        for (int i = 0; i < 10; i++) {
            System.out.print(times[i] + " ");
        }

        System.out.println();

        MaxHeap<Long> heap = new MaxHeap<>();
        heap.heapify(times);
        for (int i = 0; i < 10; i++) {
            System.out.print(times[i] + " ");
        }
    }
}
