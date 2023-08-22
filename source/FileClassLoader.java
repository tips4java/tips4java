/**
 * @(#)FileClassLoader.java	1.0 12/09/2008
 */
package darrylbu.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * FileClassLoader extends URLClassLoader to provide functionality for
 * loading a class from a file or byte array.
 * 
 * @author Darryl
 * @see URLClassLoader
 */
public class FileClassLoader extends URLClassLoader {

   /**
    * Constructs a new FileClassLoader with the default delegation parent
    * ClassLoader.  URLs can be added to the FileClassLoader using the
    * addURL(URL) method.
    * <P>
    * This constructor invokes FileClassLoader(URL[]) with an empty URL array.
    * 
    * @see #addURL(URL) 
    * @see #FileClassLoader(URL[]) 
    */
   public FileClassLoader() {
      this(new URL[]{});
   }

   /**
    * Constructs a new FileClassLoader for the specified URLs with the default
    * delegation parent ClassLoader.
    * <P>
    * This constructor invokes FileClassLoader(URL[], ClassLoader) with the
    * URLs and a null parent.
    * 
    * @param urls the URLs from which to load classes and resources
    * @see #FileClassLoader(URL[] urls, ClassLoader)
    */
   public FileClassLoader(URL[] urls) {
      this(urls, null);
   }

   /**
    * Constructs a new FileClassLoader using the specified delegation parent
    * ClassLoader.  URLs can be added to the FileClassLoader using the
    * addURL(URL) method.
    * <P>
    * When loading resources, this ClassLoader will be searched after first
    * searching in the specified parent class loader.
    * <P>
    * This constructor invokes FileClassLoader(URL[], ClassLoader) with an
    * empty URL array and the specified parent.
    * 
    * @param parent the parent class loader for delegation
    * @see #addURL(URL) 
    * @see #FileClassLoader(URL[], ClassLoader) 
    */
   public FileClassLoader(ClassLoader parent) {
      this(new URL[]{}, parent);
   }

   /**
    * Constructs a new FileClassLoader for the specified URLs using the
    * specified delegation parent ClassLoader.  The URLs will be searched in
    * the order specified for classes and resources after first searching in
    * the specified parent class loader. Any URL that ends with a '/' is
    * assumed to refer to a directory. Otherwise, the URL is assumed to refer
    * to a JAR file which will be downloaded and opened as needed.
    * 
    * @param urls the URLs from which to load classes and resources
    * @param parent the parent class loader for delegation
    * @see URLClassLoader#URLClassLoader(URL[], ClassLoader) 
    */
   public FileClassLoader(URL[] urls, ClassLoader parent) {
      super(urls, parent);
   }

   /**
    * Appends the specified URL to the list of URLs to search for classes
    * and resources. Overrides the super method to provide public access.
    * 
    * @param url the URL to be added to the search path of URLs
    * @see URLClassLoader#addURL(URL) 
    */
   @Override
   public void addURL(URL url) {
      super.addURL(url);
   }

   /**
    * Converts a file into an instance of class Class and resolves the Class
    * so it can be used.  This method extracts the file content as a byte
    * array and invokes createClass(byte[]).
    * <P>
    * The file content should have the format of a valid class file as
    * defined by the <a href="http://java.sun.com/docs/books/vmspec/">
    * Java Virtual Machine Specification</a>.
    * 
    * @param file the file to be loaded as a class
    * @return The Class object that was created from the file, or null if any
    * exception was encountered
    * @see #createClass(byte[]) 
    */
   public Class<?> createClass(File file) throws IOException {
      FileInputStream fis = null;
      try {
         fis = new FileInputStream(file);
         byte[] bytes = new byte[fis.available()];
         int read = fis.read(bytes);
         if (read != bytes.length) {
            return null;
         }
         return createClass(bytes);
      } finally {
         fis.close();
      }
   }

   /**
    * Converts an array of bytes into an instance of class Class and resolves
    * the Class so it can be used.
    * <P>
    * The byte array should have the format of a valid class file as defined
    * by the <a href="http://java.sun.com/docs/books/vmspec/">
    * Java Virtual Machine Specification</a>.
    * 
    * @param bytes The bytes that make up the class data
    * @return The Class object that was created from the byte array
    * @see ClassLoader#defineClass(String, byte[], int, int) 
    * @see ClassLoader#resolveClass(Class) 
    */
   public Class<?> createClass(byte[] bytes) {
      Class<?> clazz = defineClass(null, bytes, 0, bytes.length);
      resolveClass(clazz);
      return clazz;
   }
}
