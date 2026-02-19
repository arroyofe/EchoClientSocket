package es.ubu.lsi.echoserver;

/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.net.*;
import java.io.*;

public class EchoServer {
	
	
    public static void main(String[] args) throws IOException {
        
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        System.out.println("Escuchando por puerto: " + portNumber);
        
        try  (
            	ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
   		)
        {
            while (true){
                Socket clientSocket = serverSocket.accept();     
                System.out.println("Nuevo Cliente: " + clientSocket.getInetAddress() + "/" + clientSocket.getPort());
            	Thread hilonuevocliente = new ThreadServerHandler(clientSocket);
            	hilonuevocliente.start();
            }
        	
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
    
class ThreadServerHandler extends Thread {
	/* Como se han creado tres usuarios en pom (fernando, blas y teresa)
	 * cada una de las posiciones del array representará, quien está baneado
	 * Cuando se banee a un usuario, se recorrerá el array para ver si ya lo 
	 * está, si es así no se modifica, si no, se añade en la primera posición
	 * libre 
	 */
	private String[] baneados = {"","",""};
	
	private Socket clientSocket;
	
	public ThreadServerHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void modificarArrayBaneados(String string) {
		int num = 0;
		for (int i= 0; i<3;i++) {
			if (baneados[i]== string){
				System.out.println(string + " ya está baneado");
				num +=1;
				break;
			}
		}
		
		if(num == 0) {
			for (int i= 0; i<3;i++) {
				if (baneados[i] == null){
					System.out.println(string + " ha sido baneado");
					baneados[i]=string;
					break;
				}
			}
			
		}
		
	}

	public void run() {
		try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        	BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;
            
			while ((inputLine = in.readLine()) != null) {
            	System.out.println(clientSocket.getPort() + ":" + inputLine);
                out.println(inputLine);
                switch (inputLine.toLowerCase()) {
                	case "banear fernando":
                		modificarArrayBaneados("fernando");
                		break;
                	case "banear blas":
                		modificarArrayBaneados("blas");
                		break;
                	case "banear teresa":
                		modificarArrayBaneados("teresa");
                		break;
                }
            }
        }
        catch (IOException e) {
            System.out.println("Exception caught on thread");
            System.out.println(e.getMessage());
        }
      }
}