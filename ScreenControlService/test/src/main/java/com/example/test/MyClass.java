package com.example.test;

import com.google.gson.Gson;

public class MyClass {
    public static void main(String[] args) {
        Gson gson = new Gson();
        Apple apple = new Apple();
        String s = gson.toJson(apple);
        System.out.println(s);
        Fruit f0 = apple;
        String s1 = gson.toJson(f0);
        System.out.println(s1);
    }

   static class Fruit{
        public String Fruit1 = "f1";
        public String Fruit2 = "f2";

       @Override
       public String toString() {
           return "Fruit{" +
                   "Fruit1='" + Fruit1 + '\'' +
                   ", Fruit2='" + Fruit2 + '\'' +
                   '}';
       }
   }

    static class Apple extends Fruit{
        public String Apple1 = "a1";
        public String Apple2 = "a1";

        @Override
        public String toString() {
            return "Apple{" +
                    "Apple1='" + Apple1 + '\'' +
                    ", Apple2='" + Apple2 + '\'' +
                    '}';
        }
    }
}