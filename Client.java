import java.io.*;
import java.net.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;                          //LIBARARIES NEEDED FOR PROGRAM
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
    
    ArrayList<Server> t = new ArrayList<Server>();
    
    ArrayList<Job> j = new ArrayList<Job>();

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
    
    if(currentString.contains("JOBN")){
    j.add(newJob(currentString));
    
    sendMessage(getsCapable(j.get(0)));
    currentString = readMessage();
    sendMessage("OK");
    
    currentString = readMessage();
    t = buildServer(currentString);
    sendMessage("OK");
    currentString = readMessage();
    
    sendMessage(newAlgo(t, j));
    currentString = readMessage();
    
    j.remove(0);
}
   
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
 
 //New Algorithm for better rental cost and optimisation of average turnaround time
 public String newAlgo(ArrayList<Server> servers, ArrayList<Job> jobs){
   String infoServer = "";
   
   for(Server s: servers){
   	for(Job job: jobs){
   	  if(s.getDisk() >= job.getDiskReq() && s.getCoreCount() >= job.getCoreReq() && s.getMemory() >= job.getMemoryReq() && job.getStartTime() >= job.getRunTime()){
   	  infoServer = s.getType() + " " + s.getID();
   	  return "SCHD " + job.getJobID() + " " + infoServer;
   	  }
   	  else{
   	  infoServer = servers.get(0).getType() + " " + servers.get(0).getID();
   	  }
 	}
   }
   return "SCHD " + jobs.get(0).getJobID() + " " + infoServer;
 }
 
 //Array list of capable servers of the newAlgo algorithm is filled
 public ArrayList<Server> buildServer(String server){
   server = server.trim();
   
   ArrayList<Server> newServer = new ArrayList<Server>();
   
   String[] line = server.split("\\r?\\n"); // Create a new line
   
   for(String lines : line){
   	String[] stringSplit = lines.split("\\s+");
   	
   	//Based on disk, server ID, Core Count, Memory, server Type, new server is created
   	Server s = new Server(stringSplit[0], Integer.parseInt(stringSplit[1]), Integer.parseInt(stringSplit[4]), Integer.parseInt(stringSplit[5]), Integer.parseInt(stringSplit[6]));
   	newServer.add(s);
     }
     return newServer;
 }
 
 //Job Object
 public Job newJob(String job){
   job = job.trim();
   String[] stringSplit = job.split("\\s+");
   
   //Based on disk, job ID, core, memory, start time and run time, new job is created
   Job jobs = new Job(Integer.parseInt(stringSplit[1]), Integer.parseInt(stringSplit[2]), Integer.parseInt(stringSplit[3]), Integer.parseInt(stringSplit[4]), Integer.parseInt(stringSplit[5]), Integer.parseInt(stringSplit[6]));
   
   return jobs;
}

//Gets Capable method for servers that are based on 
public String getsCapable(Job jobs){
  return("GETS Capable " + jobs.getDiskReq() + " " + jobs.getCoreReq() + " " + jobs.getMemoryReq());
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
    public String type;
    public float rate;
    public int limit;
    public int bootupTime;
    public int coreCount;
    public int memory;
    public int disk;
    public int id;
    
  public Server(String type, int limit, int bootupTime, float rate, int coreCount, int memory, int disk){
    this.type = type;
    this.limit = limit;
    this.bootupTime = bootupTime;
    this.rate = rate;
    this.coreCount = coreCount;
    this.memory = memory;
    this.disk = disk;
   }
   
  public Server(String type, int id, int coreCount, int memory, int disk){
  this.type = type;
  this.id = id;
  this.coreCount = coreCount;
  this.memory = memory;
  this.disk = disk;
 }

  public int getDisk(){ // Disk space
    return this.disk;
  }
  
  public int getID(){ // Server ID
    return this.id;
  }
  
  public int getCoreCount(){ // CPU cores
    return this.coreCount;
  }
  
  public int getMemory(){ // RAM
    return this.memory;
  }
  
  public int getBootupTime(){ // Time taken to boot a server 
    return this.bootupTime;
  }
  
  public int getLimit(){ // Limit of servers
    return this.limit;
  }
  
  public float getRate(){ // Rate hourly
    return this.rate;
  }
  
  public String getType(){
    return this.type;
  }
 }
  
  public class Job{
   public int diskReq;
   public int jobID;
   public int coreReq;
   public int memoryReq;
   public int startTime;
   public int runTime;
   
  public Job(int diskReq, int jobID, int coreReq, int memoryReq, int startTime, int runTime){
   this.diskReq = diskReq;
   this.jobID = jobID;
   this.coreReq = coreReq;
   this.memoryReq = memoryReq;
   this.startTime = startTime;
   this.runTime = runTime;
  }
  
  public int getDiskReq(){
    return this.diskReq;
  }
  
  public int getJobID(){
    return this.jobID;
  }
  
  public int getCoreReq(){
    return this.coreReq;
  }
  
  public int getMemoryReq(){
    return this.memoryReq;
  }
  
  public int getStartTime(){
    return this.startTime;
  }
  
  public int getRunTime(){
    return this.runTime;
  }
 }
}

