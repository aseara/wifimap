package com.github.aseara.heatmap;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
public class NewtonBlack extends JPanel{
    public int DIM=1024;
    public void paint(Graphics g){
        super.paint(g);
        Color c;
        for(int i=0;i<500;i++){
            for(int j=0;j<500;j++){
                int distancei=i-250;
                int distancej=j-250;
                int k=(int)((Math.sin((distancei*distancei+distancej*distancej)/400.0)+1)*127);
                c=new Color(k%255,k%255,k%255);
                g.setColor(c);
                g.fillRect(i, j, 1, 1);
            }
        }

    }
    public static void main(String[] args) {
        JFrame jf;
        jf=new JFrame();
        jf.setSize(500, 500);
        jf.setLocation(0, 0);
        jf.add(new NewtonBlack());
        jf.setTitle("牛顿环黑白");
        jf.setResizable(false);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}