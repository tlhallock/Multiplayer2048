package org.hallock.tfe.cmn.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import org.hallock.tfe.cmn.sys.Constants;

public class Utils {

	public static final String ensureLength(String foo, char c, int depth)
	{
		StringBuilder builder = new StringBuilder();
		for (int i=foo.length(); i < depth; i++)
			builder.append(c);
		builder.append(foo);
		return builder.toString();
	}
	
	public static void attach(Container parent, Container child)
	{
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(parent);
		parent.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(child, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(child, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
	}
	
	private static final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // + "`~!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
	public static String createRandomString(int length)
	{
		StringBuilder builder = new StringBuilder(length);
		
		for (int i = 0; i < length; i++)
		{
			builder.append(characters.charAt(
					Constants.random.nextInt(characters.length())));
		}
		
		return builder.toString();
	}
	
	
	
	
	public static final String display(int number)
	{
		if (number <= 0 && !Constants.DISPLAY_ZEROS)
			return " ";
		return String.valueOf(number);
	}
	
	
	
	
	
	public static Color invertColor(Color c)
	{
		return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
	}

	public static void attachVertically(Container parent, Container[] children, int height)
		{
//			for (int i=0;i<children.length;i++)
//				children[i].setBackground(new Color(
//						(int) (Math.random() * 255),
//						(int) (Math.random() * 255),
//						(int) (Math.random() * 255)
//						));
			
	//		children[0].setBackground(new java.awt.Color(255, 255, 153));
	//		children[1].setBackground(new java.awt.Color(255, 51, 51));
	//		children[2].setBackground(new java.awt.Color(102, 0, 255));
			
//			for (int i = 0; i < children.length; i++)
//			{
//				children[i].setBounds(new Rectangle(5, 5, 50, height));
//			        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(children[i]);
//			        children[i].setLayout(jPanel1Layout);
//			        jPanel1Layout.setHorizontalGroup(
//			            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//			            .addGap(0, 0, Short.MAX_VALUE)
//			        );
//			        jPanel1Layout.setVerticalGroup(
//			            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//			            .addGap(0, height, Short.MAX_VALUE)
//			        );
//			}

		parent.removeAll();
		parent.setLayout(new GridLayout(0, 1));
		for (int i = 0; i < children.length; i++)
			parent.add(children[i]);
		
		if (true)
			return;
		
			parent.setLayout(null);
		        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(parent);
		        parent.setLayout(layout);
		        
		        ParallelGroup hgroup = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
			for (int i = 0; i < children.length; i++)
		        	hgroup = hgroup.addComponent(children[i], javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		        
	
		        SequentialGroup vgroup = layout.createSequentialGroup();
			for (int i = 0; i < children.length; i++)
				vgroup = vgroup.addComponent(children[i], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	    		                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
		        layout.setHorizontalGroup(hgroup);
		        layout.setVerticalGroup(
		        	layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		    	            .addGroup(vgroup));
	    }
}
