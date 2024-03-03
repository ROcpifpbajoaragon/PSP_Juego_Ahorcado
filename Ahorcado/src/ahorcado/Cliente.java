/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ahorcado;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Andres Moreno Blanc
 * Clase cliente que envia solicitudes al servidor y recibe las respuestas a las mismas
 * 
 */

public class Cliente {
    
    String palabraOculta, palabraElegida;
    int intentos = 10;
    
    // Dirección del servidor
    static final String HOST = "localhost";

    // Puerto en el que el servidor está escuchando
    static final int Puerto = 2000;
    
    public Cliente() {
        try {
            Scanner teclado = new Scanner(System.in);

            // Crear socket y flujos de entrada/salida
            Socket sCliente = new Socket(HOST, Puerto);
            DataInputStream flujo_entrada = new DataInputStream(sCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(sCliente.getOutputStream());
                
            // Solicitar al cliente que introduzca la dificultad del juego
            String leerDificultad = flujo_entrada.readUTF();
            System.out.print(leerDificultad);
            String dificultad = teclado.next();
            flujo_salida.writeUTF(dificultad);
            
            palabraElegida = flujo_entrada.readUTF();
            
            
            for(intentos=10; intentos>=0; intentos--){

                String mensaje = flujo_entrada.readUTF();
                System.out.print(mensaje);
                String opcion = teclado.next();
                flujo_salida.writeUTF(opcion);
                String mensajeIntentos = flujo_entrada.readUTF();
                System.out.println(mensajeIntentos);
                
                // si la opcion elegida es letra 
                if(opcion.equals("letra")){
                    
                    
                    // Lógica para que el cliente elija letra
                    System.out.println("Palabra actual: " + flujo_entrada.readUTF());
                    System.out.print(flujo_entrada.readUTF());  // Mensaje para elegir letra
                    String letra = teclado.next();
                    flujo_salida.writeUTF(letra);
                    
                    // Recibimos la palabraOculta modificada 
                    palabraOculta = flujo_entrada.readUTF();
                    
                    // Si la letra completa la palabra
                    if(palabraOculta.equals(palabraElegida)){
                        
                        palabraOculta = flujo_entrada.readUTF();
                        System.out.println(palabraOculta);
                        String mensajeRespuesta = flujo_entrada.readUTF();
                        System.out.println(mensajeRespuesta);
                        break;
                        
                    }
                    
                    // La letra no esta 
                    String mensajeRespuestaLetra = flujo_entrada.readUTF();
                    System.out.println(mensajeRespuestaLetra);
                    palabraOculta = flujo_entrada.readUTF();
                    System.out.println("Palabra actual: " + palabraOculta);
                    String letrasErroneas = flujo_entrada.readUTF();
                    System.out.println(letrasErroneas);
                    intentos = flujo_entrada.readInt();
                    
                }else if(opcion.equals("palabra")){
                    
                    palabraOculta = flujo_entrada.readUTF();
                    System.out.println(palabraOculta);
                    String mensajePalabra = flujo_entrada.readUTF();
                    System.out.print(mensajePalabra);
                    String palabra = teclado.next();
                    flujo_salida.writeUTF(palabra);
                    
                    palabraElegida = flujo_entrada.readUTF();
                    System.out.println(palabraElegida);
                    mensajePalabra = flujo_entrada.readUTF();
                    System.out.println(mensajePalabra);
                    
                }
                
            }
            
            }catch (Exception e) {
            
            // Manejar excepciones
            System.out.println(e.getMessage());
            
        }
    
    }
    
    public static void main(String[] arg) {
        // Iniciar el cliente
        new Cliente();
    }
    
}
