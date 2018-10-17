package sipudp;


import java.net.* ;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


 public class UDPClientSIP1 implements Runnable {
  
    private DatagramSocket sock2 ;
    private DatagramPacket inPacket, outPacket ;
    private String serverAddress;
    private String client1Address; 
    private int client1Port,serverPort;
    static private int phone1, phone2;
    private int i,j ;
    static Thread t,t1,t2,t5;
    static boolean f = true,x=true;
    static GUI s;
    
    static Sound son;
   
    
   
    public static void main(String [] args)
    {
 
        
        UDPClientSIP1 client = new UDPClientSIP1() ;
        t = new Thread(client);
        t.start();
        t1 = new Thread(client);
        t2 = new Thread(client);
        t5 = new Thread(client);
       
        son = new Sound();
       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                s = new GUI();
                s.setVisible(true);
                s.getButton11().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }});      
                
             s.getButton12().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
             s.getButton14().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
             
             s.getButton10().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        }); 
            
            }
     
    });
    }
    
   

    public UDPClientSIP1() {
    
       
       serverAddress = "127.0.0.1";
       serverPort = 5679;
       client1Address = "127.0.0.1";
       client1Port = 5680;
       phone1 = 4001;
    
       
       try {
              
               sock2 = new DatagramSocket(client1Port);  //Creating Client1Socket
           } catch (SocketException ex) {
               Logger.getLogger(UDPClientSIP1.class.getName()).log(Level.SEVERE, null, ex);
           }
      
    }
    
    
  
    
    public String Call_Id(){
       
        double id = Math.random();
        String str = ""+id;
        return str;
     }
    
    
    public String Gen_Tag()
    {
      double id = Math.random();
      String str = ""+id;
      return str;
    }
    

    
    public String RegisterToServer(){
        
        String str = "REGISTER sip:"+ serverAddress +" SIP/2.0\r\n" ; 
        str = str + "Call-ID: "+ client1Address +"-"+ Call_Id() +"\r\n";
        str = str + "CSeq: "+ i++ +" REGISTER\r\n" ;
        str = str + "From: <sip:"+ phone1 +"@"+ serverAddress +">;tag ="+ Gen_Tag() +"\r\n" ;
        str = str + "To: <sip:"+ phone1 +"@"+ serverAddress +">\r\n" ;
        str = str + "Via: SIP/2.0/UDP "+ client1Address +":"+ client1Port +";branch=z9hG4f564515646\r\n" ;
        str = str+  "Contact: <sip:"+ client1Port +"@"+ client1Address +":"+client1Port+">\r\n" ;
       
        return str;
        
    }
    
     public String InviteToClient(){
         
        String str = "INVITE sip:"+phone2+"@"+ serverAddress +":"+ serverPort +" SIP/2.0\r\n" ; 
        str = str + "Call-ID: "+ client1Address +"-"+ Call_Id() +"\r\n";
        str = str + "CSeq: "+ j++ +" REGISTER\r\n" ;
        str = str + "From: <sip:"+ phone1 +"@"+ serverAddress +">;tag ="+ Gen_Tag() +"\r\n" ;
        str = str + "To: <sip:"+ phone2 +"@"+ serverAddress +">\r\n" ;
        str = str + "Via: SIP/2.0/UDP "+ client1Address +":"+ client1Port +"f;branch=z9hG4f564515646\r\n" ;
        str = str+  "Contact: <sip:"+ client1Port +"@"+ client1Address +":"+serverPort+">\r\n" ;
       
     
        return str ;
        
    }

      
    @Override
    public void run() {
       
        if(Thread.currentThread() == t1){
            
            while(f){

              try{
                  
                    System.out.println("Registering.....\n");
                    s.getjTextPane1().setText("Registering...");
                    Thread.sleep(2000);

                    sendPacket(RegisterToServer());

                     Thread.sleep(2000);

                }catch(IOException ex) {
                    Logger.getLogger(UDPClientSIP1.class.getName()).log(Level.SEVERE, null, ex);
                }catch (InterruptedException ex) {
                   Logger.getLogger(UDPClientSIP1.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
         
       }
        else if(Thread.currentThread()==t2){
           
            f= true;
            if(f){
                    try{
                  
                            System.out.println("Dialing....\n");
                            s.getjTextPane1().setText("Dialing..");
                            t2.sleep(4000);
                            sendPacket(InviteToClient());
                            t2.sleep(4000);

                        }catch(IOException ex) {     
                            Logger.getLogger(UDPClientSIP1.class.getName()).log(Level.SEVERE, null, ex);
           
                        }catch (InterruptedException ex) {
                            Logger.getLogger(UDPClientSIP1.class.getName()).log(Level.SEVERE, null, ex);
                        }
                   
                }
        }
    
        else if(Thread.currentThread()==t){
           while(true){
               i=0;
               try {
                        String mess = receivePacket();
                        if(mess != null){
                            
                            System.out.println(mess);
                            f=false;
                            String s[] = mess.split("\r\n");
                            String s2  = s[0];
                            int len  = s.length-1;
                            String str1=null,str2=null;

                            if(s2.contains("SIP/2.0 200")){
                                   while(i++<len){

                                    if(s[i].contains("From"))
                                        str1 = s[i].substring(11,15);                        
                                    if(s[i].contains("To"))    
                                        str2 = s[i].substring(9,13);                        

                                }
                    
                            if(str1.equals(str2)){ 
                                this.s.getjTextPane1().setText("Registered");
                            }
                            else{
                                this.s.getjTextPane1().setText(null); 
                                String str = "Request-Line: ACK sip:"+phone2+"@"+serverAddress+":"+client1Port+" SIP/2.0\r\n";
                                for(int i=1;i<len;i++){

                                    String s3,s4;
                                    if(s[i].contains("From")){
                                        s3 = s[i].substring(11,15);
                                        str = str + "To: <sip:"+ s3 +"@"+ serverAddress +">\r\n";
                                    }  
                                    else if(s[i].contains("To")){
                                        s4 = s[i].substring(9,13);
                                         str = str + "From: <sip:"+ s4 +"@"+ serverAddress +">;tag ="+ Gen_Tag() +"\r\n" ;
                                    }
                                    else
                                     str += s[i]  + "\r\n";

                                }
                                try {
                                     sendPacket(str);  

                                } catch (UnknownHostException ex) {
                                     Logger.getLogger(UDPClientSIP1.class.getName()).log(Level.SEVERE, null, ex);
                                    }   


                               son.RTP(new Sound(),1);


                                }

                            }


                            else if(s2.contains("INVITE")){

                               String st1 = "SIP/2.0 180 Ringing\r\n";

                                for(int i=1; i<len; i++){

                                    String s3=null,s4=null;
                                     if(s[i].contains("From")){
                                        s3 = s[i].substring(11, 15);
                                        st1 = st1 + "To: <sip:"+ s3 +"@"+ serverAddress +">\r\n";
                                     } else if(s[i].contains("To")){
                                         s4 = s[i].substring(9,13); 
                                         st1 = st1 + "From: <sip:"+ s4 +"@"+ serverAddress +">;tag ="+ Gen_Tag() +"\r\n" ;    
                                     }else 
                                         st1 += s[i] + "\r\n";

                                 }

                                while(x)  {         
                                    try {

                                    sendPacket(st1); 
                                     this.s.getjTextPane1().setText("Ringing...");
                                     Thread.sleep(4000);
                                      } catch (InterruptedException ex) {
                                          Logger.getLogger(UDPClientSIP2.class.getName()).log(Level.SEVERE, null, ex);
                                      }


                                }

                                if(!x){
                                    i=0;
                                    String st = "SIP/2.0 200 OK\r\n";
                                    String s1[] = st1.split("\r\n");
                                    len = s1.length-1;
                                    while(i++<len){

                                       st += s1[i] + "\r\n";   

                                     }

                                    sendPacket(st);
                                }

                            }

                            else if(s2.contains("ACK")){

                                son.RTP(new Sound(),0);

                            } 

                            else if(s2.contains("180 Ringing")){

                                this.s.getjTextPane1().setText("Ringing...");
                            } 
                   
                        }   
                
                    }catch (IOException ex) {
                        Logger.getLogger(UDPClientSIP1.class.getName()).log(Level.SEVERE, null, ex);
                       }   
            }   
        }       
    }
    
    public void sendPacket(String str) throws UnknownHostException{
        
        byte [] data = str.getBytes() ;
        outPacket = new DatagramPacket(data, 0, data.length, InetAddress.getByName(serverAddress), serverPort) ; 
        try {      
            sock2.send(outPacket);
        } catch (IOException ex) {
            Logger.getLogger(UDPClientSIP1.class.getName()).log(Level.SEVERE, null, ex);
        }
          
    }
    
    public String receivePacket() throws SocketException, IOException{
         
        byte[] receive = new byte[600];
         
        try {
                
               inPacket = new DatagramPacket(receive,receive.length) ;
               sock2.setSoTimeout(1000);      
               sock2.receive(inPacket);
              
           } catch (SocketTimeoutException ex) {
            
             return null;
           
           }
              
        return new String(inPacket.getData());
    
    }

  
    
     static public void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {                                          
            t1.start();
        }
     
      
     static public void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        if(Integer.parseInt(s.getjTextPane1().getText())==4002){
            phone2 = Integer.parseInt(s.getjTextPane1().getText());
            t2.start();
            x=true;
        }
     }
        
                                              

    static public void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       
        s.getjTextPane1().setText(null);              
        x = false;
    
    }                                         

    static public void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        son.close();
    
    }


 }
