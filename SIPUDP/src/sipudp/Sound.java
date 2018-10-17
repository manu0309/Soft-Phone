/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sipudp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author vccs
 */
public class Sound implements Runnable {

    float sampleRate = 8000;
    int sampleSizeInBits = 8;
    int channels = 1;
    boolean signed = true;
    boolean bigEndian = false;
    File file;
    AudioInputStream ais;
    TargetDataLine line;
    SourceDataLine srcLine;
    DatagramSocket sock;
    DatagramPacket outPacket, inPacket;
    static Thread t3, t4;
    InetAddress clientAddress = InetAddress.getLoopbackAddress();
    AudioFormat format;
    int clientPort=5000;

   
    /**
     * @param args the command line arguments
     */
  /*  public static void main(String[] args) {
        Sound s = new Sound();
        s.openSrcDataLine();
        t3 = new Thread(s);
        t3.start();
        t4 = new Thread(s);
        t4.start();

    }*/
    
    public void RTP(Sound s,int i){
        //Sound s = new Sound()
        
       
        t3 = new Thread(s);
        t4 = new Thread(s);
  
      //  this.sock = sock;
     //   clientPort = cp;
           s.openSrcDataLine();
        t3.start();
       
       
           
        t4.start();
    } 
    public void close(){
        t3.stop();
        t4.stop();
    }    
    
    
    public void openSrcDataLine() {

      try {
            sock = new DatagramSocket(clientPort);

        } catch (SocketException ex) {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            srcLine = (SourceDataLine) AudioSystem.getLine(info);
            srcLine.open(format);
            srcLine.start();

        } catch (LineUnavailableException ex) {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        if (Thread.currentThread() == t3) {
           
 
                
             format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                try {
                    line = (TargetDataLine) AudioSystem.getLine(info);

                    line.open(format);
                    line.start();

                    int numBytesRead;

                    byte[] buff1 = new byte[12];
                    byte[] buff2 = new byte[172];
                    byte[] buff = new byte[172];
                    while (true) {
                     
                        numBytesRead = line.read(buff, 0, buff.length);
                        System.out.println("Bytes Read:" + numBytesRead);
                        System.out.println("Buffer: " + buff);
                        try {

                            outPacket = new DatagramPacket(buff, 0, buff.length, clientAddress, clientPort);
                            sock.send(outPacket);

                        } catch (Exception ex) {
                        }

                    }

                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        } else if(Thread.currentThread() == t4) {

            while (true) {
                byte[] receive = new byte[172];

                inPacket = new DatagramPacket(receive, receive.length);
                try {
                  
                     sock.receive(inPacket);
                } catch (IOException ex) {
                    Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
                }
                for (int i = 0; i < receive.length; i++) {
                    System.out.print(receive[i] + " ");

                }
                ByteArrayInputStream bals = new ByteArrayInputStream(receive);
                ais = new AudioInputStream(bals, format, receive.length);

                System.out.println(" ");
                srcLine.write(receive, 0, receive.length);
            }
        }
    }
}
