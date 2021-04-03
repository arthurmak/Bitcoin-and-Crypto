

import java.util.ArrayList;


public class ImageList {
  String [] image;

  public static void main(String[] args) {
    ImageList curList = new ImageList();
    curList.printList(curList.image);
    //printList();
  }

  void ImageList() {
    String[] image = {"no image list", "nil"};
    printList(image);
    //System.out.println("pls provide image lists");

  }

  void ImageList(String[] a) {
    String[] image = a;
    printList(image);
    //for (int i = 0; i < a.length; i++) {
      //System.out.println(image[i]);
    //}
  }

  static void printList(String[] image) {

    for (int i = 0; i < image.length; i++) {
      System.out.println(image[i]);
  //image = args;
  //System.out.println(image[0]);
    }

  }
}
