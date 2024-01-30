/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ahorcado;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Andres Moreno Blanc
 * Clase que maneja el servidor, encargado de recibir las solicitudes de los clientes 
 * efectuar las gestiones de estas solicitudes y devolver respuesta al cliente
 */

public class Servidor extends Thread { // extend Thread permite trabajar al servidor con varios clientes diferenciados en hilos
    
    // Declaracion de variables 
    Socket skCliente;
    static final int Puerto = 2000;
    
    // Constructor que recibe el socket del cliente al que se va a atender
    public Servidor(Socket sCliente) {
        skCliente = sCliente;
    }
    
    // Método principal del servidor
    public static void main(String[] arg) {
        try {
            
            // Inicio el servidor en el puerto
            ServerSocket skServidor = new ServerSocket(Puerto);
            System.out.println("Escucho el puerto " + Puerto);

            while (true) {
                // Se conecta un cliente
                Socket skCliente = skServidor.accept();
                System.out.println("Cliente conectado");

                // Atiendo al cliente mediante un thread
                new Servidor(skCliente).start();
            }
        } catch (Exception e) {
            // Manejar excepciones
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
    
    public void run(){
        
        try {
            
            // Creo los flujos de entrada y salida para comunicarme con el cliente
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());
            
            // Solicitar al cliente que introduzca el nivel de dificultad
            flujo_salida.writeUTF("Selecciona la dificultad(facil, medio, dificil) ");
            String nivel = flujo_entrada.readUTF();
            System.out.println("\tEl cliente ha dicho " + nivel);
            
        }catch (Exception e) {
            
            // Manejar excepciones
            System.out.println(e.getMessage());
            
        }
            
        
    }
    
}
