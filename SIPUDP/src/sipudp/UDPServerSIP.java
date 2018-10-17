
package sipudp;

import java.io.IOException;
import java.net.* ;
import java.util.ArrayList;

public class UDPServerSIP {
    
   String HEADER ;
   RegInfo info ;
   ArrayList<RegInfo> list ;
   DatagramSocket sock;
   DatagramPacket p,q ;
   String message ;
   ArrayList<RegInfo> regis ;

   int ClientPort;
   final int port = 5679 ;   //Server Port
 
  public static void main(String[] args) throws SocketException, IOException {
       UDPServerSIP  sudp = new UDPServerSIP() ;
       sudp.go() ;
  }
   
  
  UDPServerSIP() throws SocketException 
  {
     sock = new DatagramSocket(port) ;  //Creating the Server Socket
     list = new ArrayList<RegInfo>() ;

  }
    
    
   public void go() throws IOException 
   {
       byte[] receive = new byte[600];
     
       while(true) 
       {
          p = new DatagramPacket(receive, receive.length) ;
          sock.receive(p);
          String msg = new String(p.getData(),p.getOffset(),p.getLength()) ;
         
          System.out.println(msg);
                   
          PARSE(msg) ;
          
      }
   }
   
   public void SendPacket(String str,int port) throws IOException
   {
         byte[] data = str.getBytes();
         q = new DatagramPacket(data,data.length, InetAddress.getLoopbackAddress(),port) ; //client port
         sock.send(q);
       
   }
   
   
    public void PARSE(String mes) throws IOException 
    {  
        info = new RegInfo() ; 
        String s[] = mes.split("\r\n");
        String s2  = s[0];
        int len  = s.length-1; //7
       
        int i=0;
        int j = 0;
        HEADER = "SIP/2.0 200 OK\r\n";
     
        if(s2.contains("REGISTER")){
            while(i++<len){
                if(s[i].contains("Via"))
                {
                    String s4 = s[i].substring(17, 31).trim() ;
                    String [] s5 = s4.split(":") ;
                    info.setRegIp(s5[0]) ;
                    info.setRegPort(s5[1]);
                    
                 
                }
                
               if(s[i].contains("From")){
                   String s4 = s[i].substring(11,15);
                    info.setphone(s4);
                  
                   
               }      
            }
             list.add(info);
             System.out.println("IP ADDR : "+(list.get(j)).getRegIp() +" Port :"+(list.get(j)).getRegPort()+" Phone :"+(list.get(j)).getphone()+"\n");
             
           String s6 = HEADER ;
             for(i = 1 ; i < len ; i++)
                s6 += s[i] + "\r\n" ;
             
             
           try{
           SendPacket(s6,Integer.parseInt(info.getRegPort()));  
           }catch(IOException ex)
           {}
        }
        
        else if(s2.contains("INVITE"))
          {  
             
              
            while(i++<len){
                if(s[i].contains("To")){
                    String s4 = s[i].substring(9,13).trim();
                   
                     RegInfo info1 = list.get(0);
                    RegInfo info2 =  list.get(1);
                    
                    if(info1.getphone().equals(s4)){
                        ClientPort = Integer.parseInt(info1.getRegPort()); 
                    }
                    else  if(info2.getphone().equals(s4)){
                        ClientPort = Integer.parseInt(info2.getRegPort()); 
                    }
                    else
                    {
                        System.out.println("\nNot Registered\n");
                    }       
                    
                }}         
             
        try{
            SendPacket(mes,ClientPort);  
           }catch(IOException ex)
           {}
        }
          
          
        else if(s2.contains("180 Ringing"))
          {  
              
            
            while(i++<len){
                         
               
                if(s[i].contains("To"))
                {
                    String s4 = s[i].substring(9,13).trim();
                    RegInfo info1 = list.get(0);
                    RegInfo info2 = list.get(1);
                    if(info1.getphone().equals(s4)){
                        ClientPort = Integer.parseInt(info1.getRegPort()); 
                    }
                    else  if(info2.getphone().equals(s4)){
                        ClientPort = Integer.parseInt(info2.getRegPort()); 
                    }
                    else
                    {
                        System.out.println("\nNot Registered\n");
                    }
                    
                    
                }
            }         
                
            try{


                SendPacket(mes,ClientPort); 
         
         
           }catch(IOException ex)
           {}
              
        }
             
        else if(s2.contains("ACK"))
        {
               
            while(i++<len){
                if(s[i].contains("To"))  {  
                    String s4 = s[i].substring(9,13).trim();
                    RegInfo info1 = list.get(0);
                    RegInfo info2 = list.get(1);
                    if(info1.getphone().equals(s4)){
                        ClientPort = Integer.parseInt(info1.getRegPort()); 
                    }
                    else if(info2.getphone().equals(s4)){
                        ClientPort = Integer.parseInt(info2.getRegPort()); 
                    }
                    else{
                        System.out.println("\nNot Registered\n");
                    }
                    
                } 
            }        
             
            try{
               SendPacket(mes,ClientPort); 
           }catch(IOException ex)
           {}
        }  
            
        else if(s2.contains("200 OK"))  { 

            while(i++<len){
                 if(s[i].contains("To")){       
                     String s4 = s[i].substring(9,13).trim();
                     RegInfo info1 = list.get(0);
                     RegInfo info2 = list.get(1);
                     if(info1.getphone().equals(s4)){
                         ClientPort = Integer.parseInt(info1.getRegPort()); 
                     }
                     else  if(info2.getphone().equals(s4)){
                         ClientPort = Integer.parseInt(info2.getRegPort()); 
                     }
                     else
                     {
                         System.out.println("\nNot Registered\n");
                     }

                     }   
             }

            try{
                SendPacket(mes,ClientPort);
            }catch(IOException ex){ }


        }   
        else if(s2.contains("BYE")){

             while(i++<len){
                        if(s[i].contains("To"))  {       
                   String s4 = s[i].substring(9,13).trim();
                     RegInfo info1 = list.get(0);
                        RegInfo info2 = list.get(1);
                        if(info1.getphone().equals(s4)){
                            ClientPort = Integer.parseInt(info1.getRegPort()); 
                        }
                        else  if(info2.getphone().equals(s4)){
                            ClientPort = Integer.parseInt(info2.getRegPort()); 
                        }
                        else
                        {
                            System.out.println("\nNot Registered\n");
                        }

                    }   }

                         try{
              SendPacket(mes,ClientPort);
               }catch(IOException ex)
               {}    
        }
    } 
    
}
    
    
    



















