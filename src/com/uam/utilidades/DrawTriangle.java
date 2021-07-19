package com.uam.utilidades;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author oximetalona
 */
public class DrawTriangle extends Frame{

    @Override
    public void paint(Graphics g) {
        Graphics2D ga = (Graphics2D) g;
        ga.setPaint(Color.blue);
        Polygon po = new Polygon(new int [] {50,100,0}, new int []{0,100,100}, 3);
        ga.fillPolygon(po);
        
    }
    
    private void savePng(Polygon polygon) throws IOException{
        int width = 200, height = 200;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D ga = bi.createGraphics();
        ga.setPaint(Color.blue);
        ga.fillPolygon(polygon);
        ImageIO.write(bi, "PNG", new File("yourImageName.png"));
    }
    
    public static void main(String[] args) {
      
     DrawTriangle frame = new DrawTriangle();
      
     frame.addWindowListener(
      new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent we)
         {
            System.exit(0);
         }
      }
     );

        frame.setSize(200 , 200);
        
        frame.setVisible(true);
        
    }
 
}
