/*
 * Demo application using USBtinLib, the Java Library for USBtin
 * http://www.fischl.de/usbtin
 *
 * Copyright (C) 2014  Thomas Fischl 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package USBtinLibDemo;

import de.fischl.usbtin.*;

/**
 * Demo application using USBtinLib, the Java Library for USBtin.
 * 
 * @author Thomas Fischl
 */
public class USBtinLibDemo implements CANMessageListener {

    /** CAN message identifier we look for */
    static final int WATCHID = 0x002;
    
    /**
     * This method is called every time a CAN message is received.
     * 
     * @param canmsg Received CAN message
     */
    @Override
    public void receiveCANMessage(CANMessage canmsg) {

        // In this example we look for CAN messages with given ID
        if (canmsg.getId() == WATCHID) {
            
            // juhuu.. match!
            
            // print out message infos
            System.out.println("Watched message: " + canmsg);
            System.out.println(
                    "  id:" + canmsg.getId()
                    + " dlc:" + canmsg.getData().length
                    + " ext:" + canmsg.isExtended()
                    + " rtr:" + canmsg.isRtr());
            
            // and now print payload
            for (byte b : canmsg.getData()) {
                System.out.print(" " + b);
            }
            System.out.println();
            
        } else {
            // no match, just print the message string
            System.out.println(canmsg);
        }
    }

    /**
     * Entry method for our demo programm
     * 
     * @param args Arguments
     */
    public static void main(String[] args) {
        
        try {

            // create the instances
            USBtin usbtin = new USBtin();
            USBtinLibDemo libDemo = new USBtinLibDemo();

            // connect to USBtin and open CAN channel with 10kBaud in Active-Mode
            usbtin.connect("/dev/ttyACM1"); // Windows e.g. "COM3"
            usbtin.addMessageListener(libDemo);
            
            usbtin.openCANChannel(10000, USBtin.OpenMode.ACTIVE);

            // send an example CAN message (standard)
            usbtin.send(new CANMessage(0x100, new byte[]{0x11, 0x22, 0x33}));
            // send an example CAN message (extended)
            usbtin.send(new CANMessage(0x101, new byte[]{0x44}, true, false));

            // now wait for user input
            System.out.println("Listen for CAN messages (watch id=" + WATCHID + ") ... press ENTER to exit!");
            System.in.read();

            // close the CAN channel and close the connection
            usbtin.closeCANChannel();
            usbtin.disconnect();

        } catch (USBtinException ex) {
            
            // Ohh.. something goes wrong while accessing/talking to USBtin           
            System.err.println(ex);            
            
        } catch (java.io.IOException ex) {
            
            // this we need because of the System.in.read()
            System.err.println(ex);
        }
    }
}
