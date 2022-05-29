import java.io.*;
import java.net.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;                          //LIBARARIES NEEDED FOR PROGRAM
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
  
 // initializations 
    private static Socket socket = null;
    private static BufferedReader din = null;
    private static DataOutputStream dout = null;
    private String currentString;

  public static void main(String[] args) {
//Run the program and connection to address 127.0.0.1 and port 50000
    Client client = new Client("127.0.0.1", 50000);
    client.run();
  }


  public Client(String address, int port) {
    // Connection Establishment
    try
    {
      // Socket Creation
      socket = new Socket(address, port);
      
      //Receive input
      din = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      
      //Output
      dout = new DataOutputStream(socket.getOutputStream());
    }catch(UnknownHostException u){
      System.out.println("ERR");
      System.out.println(u);
    } catch(IOException i) {
      System.out.println("ERR");
      System.out.println(i);
    }
  }
  public void run() {
  
    try{
    //HELO message
    sendMessage("HELO\n");
    System.out.println("Client says HELO");
    currentString = readMessage();
    System.out.println("Server replies " + currentString);
    
    //AUTH message
    System.out.println("SEND AUTH");
    String username = System.getProperty("user.name");
    dout.write(("AUTH" + username + "\n").getBytes());
    dout.flush();
    currentString = readMessage();
    System.out.println("Welcome " + username);

    //REDY message
    dout.write(("REDY\n").getBytes());
    dout.flush();
    currentString = readMessage();
    System.out.println("message = " + currentString + "\n");
    
    //Split String so we get last 3 from JOBN
    String[] jobSplit = currentString.split("\\s+");
    dout.write(("GETS Capable " + jobSplit[4] + " " + jobSplit[5] + " " + jobSplit[6] + "\n").getBytes());
    dout.flush();
    currentString = readMessage();
    System.out.println("message = " + currentString);
    
    //OK message
    dout.write(("OK\n").getBytes());
    dout.flush();
    currentString = readMessage();
    System.out.println(currentString);
    
    //If message contains JOBN undergo scheduling 
    int count = 0;
    if(currentString.contains("JOBN") && count <10) {
    System.out.println("SCHD" + count + "super-silk 0\n");
    dout.flush();
    currentString = readMessage();
    count++;
    }
    
    //Quit Simulation Otherwise
    quitSimulation();
  }
  catch(IOException i){
  System.out.println(i);
  }
 }
  

  //Quit Simulation method
  public void quitSimulation() {
    try {
      // send "QUIT"
      sendMessage("OK\n");
      currentString = readMessage();

      // if recieved "QUIT", closes connection
      if (currentString.equals("QUIT")) {
        socket.close();
        din.close();
        dout.close();
      }
    } catch (IOException i) {
      System.out.println("ERR");
      System.out.println(i);
    }
  }

  //Send message method for messages
  private void sendMessage(String message) {
    // convert message to bytes
    try{
    byte[] messageByte = message.getBytes();
    dout.write(messageByte);
    dout.flush();
    }
    catch (IOException i) {
      System.out.println("ERR");
      System.out.println(i);
    }
  }

  //Read message method to read messages for input
  private String readMessage() {
    String messageIn = "";
    try {
      while (!din.ready()) {
      }
      while (din.ready()) {
        messageIn += (char) din.read();
      }
      currentString = messageIn;

    } catch (IOException i) {
      System.out.println("ERR");
      System.out.println(i);
    }
    return messageIn;
  }
  
  public class Server {
    public int disk;
    public int id;
    public int coreCount;
    public int memory;
    public int bootupTime
    public int limit;
    public float rate;
    public String type;
    
  Server(int disk, int id, int coreCount, int memory, int bootupTime, int limit, float rate, String type)
    this.disk = disk;
    this.id = id;
    this.coreCount = coreCount;
    this.memory = memory;
    this.bootupTime = bootupTime;
    this.limit = limit;
    this.rate = rate;
    this.type = type;
  }
}
