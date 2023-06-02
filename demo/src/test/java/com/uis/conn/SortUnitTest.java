/*
 * Copyright (c) 2021 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;

public class SortUnitTest {

    long mills = 0L;
    public int[] getInput(){
        int size = 10;//100000
        int a[] = new int[size];
        Random random = new Random();
        for(int i=0;i<size;i++){
            a[i] = random.nextInt(size);
        }
        print(a,"原数据");
        mills = System.currentTimeMillis();
        return a;
    }

    public void costTime(){
        System.out.println("排序耗时:"+(System.currentTimeMillis()-mills)+"ms");
    }

    //输出数组
    public void print(int[] a,String title){
        System.out.println(title+":");
        for(int i:a){
            System.out.print(i+" ");
        }
        System.out.println();
    }

    /**
     * 交换数组2个位置数据
     * @param a 输入数组
     * @param i 交换数组下标
     * @param j 交换数组下标
     */
    public void swap(int[] a,int i,int j){
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    @Test
    public void sortTest(){
        bubble();
        select();
        insert();
        shell();
        heap();
        quick();
        merge();
    }

    //冒泡排序
    @Test
    public void bubble(){
        int[] a = getInput();
        int len = a == null ? 0 : a.length;
        for(int i=0;i<len-1;i++){
            for(int j=len-1;j>i;j--){
                if(a[j] < a[j-1]){
                    swap(a,j,j-1);
                }
            }
            //print(a,"第"+(i+1)+"次排序");
        }
        costTime();
        print(a,"冒泡排序");
    }

    //选择排序
    @Test
    public void select(){
        int[] a = getInput();
        int len = a == null ? 0 : a.length;
        for(int i=0;i<len-1;i++){
            int k = i;
            for(int j=i+1;j<len;j++){
                if(a[j]<a[k]){
                    k = j;
                }
            }
            swap(a,i,k);
        }
        costTime();
        print(a,"选择排序");
    }

    //插入排序
    @Test
    public void insert(){
        int[] a = getInput();
        int len = a == null ? 0 : a.length;
        for(int i=1;i<len;i++){
            int j= i-1;
            int t = a[i];
            while(j>=0 && a[j]>t){
                a[j+1] = a[j];
                j--;
            }
            a[j+1] = t;
        }
        costTime();
        print(a,"插入排序");
    }

    //shell排序
    @Test
    public void shell(){
        int[] a = getInput();
        int len = a == null ? 0 : a.length;
        for(int r=len/2;r>0;r/=2){
            for(int i=r;i<len;i+=r){
                int j= i-r;
                int t = a[i];
                while(j>=0 && a[j]>t){
                    a[j+r] = a[j];
                    j-=r;
                }
                a[j+r] = t;
            }
        }
        costTime();
        print(a,"shell排序");
    }

    //堆排序
    @Test
    public void heap(){
        int[] a = getInput();
        int len = a == null ? 0 : a.length;
        buildHeap(a,len);
        int index = len;
        for(int i=len-1;i>0;i--){
            swap(a,i,0);
            index--;
            maxHeap(a,index,0);
        }
        costTime();
        print(a,"堆排序");
    }

    public void buildHeap(int[] a,int len){
        //构建从二叉树最后的根结点开始
        for(int i=len/2;i>=0;i--){
            maxHeap(a,len,i);
        }
    }

    public void maxHeap(int[] a,int len,int i){
        int l = 2*i+1;
        int r = l+1;
        int max;
        if(l<len && a[l]>a[i]){
            max = l;
        }else {
             max = i;
        }
        if(r<len && a[r]>a[max]){
            max = r;
        }
        if(max != i){
            swap(a,i,max);
            maxHeap(a,len,max);
        }
    }

    //合并排序
    @Test
    public void merge(){
        int[] a = getInput();
        int len = a == null ? 0 : a.length;
        splitArray(a,0,len-1);
        costTime();
        print(a,"合并排序");
    }

    public void splitArray(int[] a,int p,int r){
        if(p<r){
            int q = (r+p)/2;
            splitArray(a,p,q);
            splitArray(a,q+1,r);
            mergeArray(a,p,q,r);
        }
    }

    public void mergeArray(int[] a,int p,int q,int r){
        int n1 = q-p+1;
        int n2 = r-q;
        int[] L = new int[n1];
        int[] R = new int[n2];
        for(int i=0;i<n1;i++){
            L[i] = a[p+i];
        }
        for(int i=0;i<n2;i++){
            R[i] = a[q+i+1];
        }
        int t1 = 0;
        int t2 = 0;
        for(int i=p;i<=r;i++){
            if(t2>=n2 || ( t1<n1 && L[t1]<R[t2]) ){
                a[i] = L[t1];
                t1++;
            }else{
                a[i] = R[t2];
                t2++;
            }
        }
    }

    //快速排序
    @Test
    public void quick(){
        int[] a = getInput();
        int len = a == null ? 0 : a.length;
        depart(a,0,len-1);
        costTime();
        print(a,"快速排序");
    }

    public void depart(int[] a,int p,int r){
        if(p<r){
            swapIndex(a,p,r);
            int q = quickly(a,p,r);
            depart(a,p,q-1);
            depart(a,q+1,r);
        }
    }

    //应对有序数据时加速排序，减少递归
    public void swapIndex(int[] a,int p,int r){
        int q = (r+p)/2;
        swap(a,q,r);
    }

    public int quickly(int[] a,int p,int r){
        int t = a[r];
        int j = p-1;
        for(int i=p;i<r;i++){
            if(a[i]<t){
                j++;
                swap(a,j,i);
            }
        }
        swap(a,j+1,r);
        return j+1;
    }
}
