package com.uis.conn;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void sort() throws Exception {
        bubbleSort();
        selectSort();
        insertSort();
        shellSort();
        mergeSort();
        quickSort();

        heapSort();
    }

    int[] inputs = {100, 120, 108, 109, 110, 160, 99, 89, 30, 150, 119, 200, 50, 33};

    public int[] getInput() {
        int s = inputs.length;
        int[] data = new int[s];
        for (int i = 0; i < s; i++) {
            data[i] = inputs[i];
        }
        return data;
    }

    //数据交换
    public void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public void print(int[] a,String title) {
        System.out.print(title+":\t");
        for(int num:a) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    //冒泡排序
    @Test
    public void bubbleSort() throws Exception {
        int[] a = getInput();
        int size = a.length;
        for(int i=0;i<size-1;i++){
            for(int k=size-1;k>i;k--){
                if(a[k]<a[k-1]){
                    swap(a,k,k-1);
                }
            }
        }
        print(a,"冒泡排序");
        bubbleDown();
    }

    public void bubbleDown() {
        int[] a = getInput();
        int size = a.length;
        for(int i=0;i<size-1;i++){
            for(int j=size-1;j>i;j--){
                if(a[j]>a[j-1]){
                    swap(a,j,j-1);
                }
            }
        }
        print(a,"冒泡逆排序");
    }

    //选择排序
    @Test
    public void selectSort() throws Exception {
        int[] input = getInput();
        int size = input.length;
        for(int i=0;i<size-1;i++){
            int k = i;
            for(int j=i+1;j<size;j++){
                if(input[j]<input[k]){
                    k = j;
                }
            }
            if(k!=i){
                swap(input,i,k);
            }
        }
        print(input,"选择排序");
        selectDown();
    }

    public void selectDown(){
        int[] a = getInput();
        int size = a.length;
        for(int i=0;i<size-1;i++){
            int k = i;
            for(int j=i;j<size;j++){
                if(a[j]>a[k]){
                    k = j;
                }
            }
            if(k!=i) {
                swap(a, k, i);
            }
        }
        print(a,"选择逆排序");
    }

    //插入排序
    @Test
    public void insertSort() throws Exception {
        int[] input = getInput();
        int size = input.length;
        for(int i=1;i<size;i++){
            int k = i-1;
            int temp = input[i];
            while(k>=0 && temp<input[k]){
                input[k+1] = input[k];
                k--;
            }
            if(1+k != i){
                input[k+1] = temp;
            }
        }
        print(input,"插入排序");
        insertDown();
    }

    public void insertDown(){
        int[] a = getInput();
        int size = a.length;
        for(int i=1;i<size;i++){
            int j = i-1;
            int tmp = a[i];
            while(j>=0 && a[j]<tmp){
                a[j+1] = a[j];
                j--;
            }
            a[j+1] = tmp;
        }
        print(a,"插入逆排序");
    }

    //shell排序
    @Test
    public void shellSort() throws Exception {
        int[] input = getInput();
        int size = input.length;
        for(int r=size/2;r>=1;r/=2){
            for(int i=r;i<size;i++){
                int k = i-r;
                int temp = input[i];
                while(k>=0 && temp<input[k]){
                    input[k+r] = input[k];
                    k-=r;
                }
                if(r+k != i){
                    input[k+r] = temp;
                }
            }
        }
        print(input,"Shell排序");
    }

    //快速排序
    @Test
    public void quickSort() throws Exception {
        int[] input = getInput();
        int size = input.length;
        quick(input,0,size-1);
        print(input,"快速排序");
        quickDown(input,0,size-1);
        print(input,"快速逆排序");
    }

    public void quick(int[] a,int p,int r){
        if(p<r){
            int q = partition(a,p,r);
            quick(a,p,q-1);
            quick(a,q+1,r);
        }
    }

    public int partition(int[] a,int p,int r){
        int x = a[r];
        int i = p-1;
        for(int j=p;j<r;j++){
            if(a[j]<x){
                i++;
                swap(a,i,j);
            }
        }
        swap(a,i+1,r);
        return i+1;
    }

    public void quickDown(int[] a,int p,int r){
        if(p<r)
        {
            int q = partitionD(a,p,r);
            quickDown(a,p,q-1);
            quickDown(a,q+1,r);
        }
    }

    public int partitionD(int[] a,int p,int r){
        int x = a[r];
        int i = p-1;
        for(int j=p;j<r;j++){
            if(a[j]>x){
                i++;
                swap(a,i,j);
            }
        }
        swap(a,i+1,r);
        return i+1;
    }

    //堆排序
    @Test
    public void heapSort() throws Exception {
        int[] a = getInput();
        int size = a.length;
        buildMaxHeap(a);
        print(a,"堆排序");
        for(int i=size-1;i>0;i--){
            swap(a,0,i);
            size--;
            maxHeap(a,size,0);
        }
        print(a,"堆排序");
    }

    public void buildMaxHeap(int[] a){
        int hSize = a.length;
        for(int i=hSize/2;i>0;i--){
            maxHeap(a,hSize,i-1);
        }
    }

    public void maxHeap(int[] a,int hSize,int i){
        int l = 2*i+1;
        int r = l+1;
        int largest;
        if(l<hSize && a[l]>a[i]){
            largest = l;
        }else{
            largest = i;
        }
        if(r<hSize && a[r]>a[largest]){
            largest = r;
        }
        if(largest != i){
            swap(a,i,largest);
            maxHeap(a,hSize,largest);
        }
    }

    //合并排序
    @Test
    public void mergeSort() throws Exception {
        int[] input = getInput();
        int size = input.length;
        merge(input,0,size-1);
        print(input,"合并排序");
    }

    public void merge(int a[],int p,int r){
        if(p<r){
            int q = (p+r)/2;
            merge(a,p,q);
            merge(a,q+1,r);
            merge(a,p,q,r);
        }
    }

    //有序数组合并排序(无哨兵)
    public void merge(int a[],int p,int q,int r){
        int nL = q-p+1;
        int[] L = new int[nL];
        for(int i=0;i<nL;i++){
            L[i] = a[p+i];
        }
        int nR = r-q;
        int[] R = new int[nR];
        for(int i=0;i<nR;i++){
            R[i] = a[q+i+1];
        }
        int n1=0,n2=0;
        for(int i=p;i<=r;i++){
            if(n2>=nR || (n1<nL && L[n1]<R[n2])){
                a[i] = L[n1];
                n1++;
            }else{
                a[i] = R[n2];
                n2++;
            }
        }
    }
    //有序数组合并排序(带哨兵)
    public void mergeMark(int a[],int p,int q,int r){
        int n1 = q-p+1;
        int n2 = r-q;
        int[] L = new int[n1+1];
        int[] R = new int[n2+1];
        for(int i=0;i<n1;i++){
            L[i] = a[p+i];
        }
        for(int i=0;i<n2;i++){
            R[i] = a[q+i+1];
        }
        L[n1] = Integer.MAX_VALUE;
        R[n2] = Integer.MAX_VALUE;
        int i=0;
        int j=0;
        int k = p;
        for(;k<r+1;k++){
            if(L[i]<=R[j]){
                a[k] = L[i];
                i++;
            }else{
                a[k] = R[j];
                j++;
            }
        }
    }
}