package com.lp.springboot1.objectTobytearray;
import java.io.*;

public class objectToByteArray {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

       Person person1 =  new Person("aaa",12);
       ByteArrayOutputStream bos = new ByteArrayOutputStream();
       ObjectOutputStream oos = new ObjectOutputStream(bos);
       oos.writeObject(person1);

       //得到person对象的byte数组
       byte[] personByteArray = bos.toByteArray();
        System.out.println(personByteArray.length);

        //从byte数组中还原person对象
        ByteArrayInputStream bis = new ByteArrayInputStream(personByteArray);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Person restorePerson = (Person) ois.readObject();
        System.out.println(restorePerson);


    }
}
