/**
 * PROJECT   : Elbfisch - java process automation controller (jPac)
 * MODULE    : ServerMain.java
 * VERSION   : -
 * DATE      : -
 * PURPOSE   : 
 * AUTHOR    : Bernd Schuster, MSK Gesellschaft fuer Automatisierung mbH, Schenefeld
 * REMARKS   : -
 * CHANGES   : CH#n <Kuerzel> <datum> <Beschreibung>
 *
 * This file is part of the jPac process automation controller.
 * jPac is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jPac is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the jPac If not, see <http://www.gnu.org/licenses/>.
 */

package elbfisch.samples.opcua.server;

import org.jpac.CharString;
import org.jpac.Decimal;
import org.jpac.EventTimedoutException;
import org.jpac.ImpossibleEvent;
import org.jpac.InputInterlockException;
import org.jpac.Logical;
import org.jpac.Module;
import org.jpac.OutputInterlockException;
import org.jpac.PeriodOfTime;
import org.jpac.ProcessException;
import org.jpac.ShutdownRequestException;
import org.jpac.SignalInvalidException;
import org.jpac.SignedInteger;
import org.jpac.vioss.opcua.Handshake;

public class ServerMain extends Module{
    final long    HANDSHAKETIMEOUT = 10 * sec;     
    
    Logical       toggle;
    SignedInteger lastCommand;
    Decimal       analogValue;
    CharString    comment;
    Handshake     handshake; 
    PeriodOfTime  busyTime = new PeriodOfTime(1 * sec);
    int           result   = 0;
    
    public ServerMain(){
        super(null, "Main");
        try{           
            toggle      = new Logical(this,"toggle" ,false);
            lastCommand = new SignedInteger(this,"lastCommand");
            analogValue = new Decimal(this,"analogValue",0.0);
            comment     = new CharString(this,"comment","");
            handshake   = new Handshake(this, "handshake", null);
        }
        catch(Exception exc){
            Log.error("Error:", exc);
        }
    }

    protected void Xwork() throws ProcessException {
        int cmd = 0;
        try{
            Log.info("started");
            while(true){
                try{
                    //lastCommand.set(cmd++);
                    new ImpossibleEvent().await();
                }
                catch(ShutdownRequestException exc){
                    throw exc;
                }
                catch(EventTimedoutException exc){
                    Log.error("client failed to remove handshake signals");
                    handshake.resetAcknowledgement();
                    handshake.resetRequest();
                }
                catch(SignalInvalidException exc){
                    Log.error("client might have asynchronously closed the session");
                    handshake.resetAcknowledgement();
                    handshake.resetRequest();
                }
            }
        }
        finally{
            Log.info("finished");
        }
    }
    
    protected void work() throws ProcessException {
        try{
            Log.info("started");
            while(true){
                try{
                    if (!handshake.isValid()){
                    	Log.info("awaiting handshake getting valid");
                        handshake.resetAcknowledgement();
                        handshake.valid().await();
                    	Log.info("handshake got valid. Offer service ...");
                    }
                    handshake.setReady(true);
                    handshake.requested().await();
                    handshake.setActive(true);
                    handshake.setReady(false);
                    handleRequest(handshake.getCommand());
                    handshake.acknowledge(result);
                    handshake.requestRemoved().await(HANDSHAKETIMEOUT);
                    handshake.resetAcknowledgement();
                    busyTime.await();
                }
                catch(ShutdownRequestException exc){
                    throw exc;
                }
                catch(EventTimedoutException exc){
                    Log.error("client failed to remove handshake signals");
                    handshake.resetAcknowledgement();
                    handshake.resetRequest();
                }
                catch(SignalInvalidException exc){
                    Log.error("client might have asynchronously closed the session");
                    handshake.resetAcknowledgement();
                    handshake.resetRequest();
                }
            }
        }
        finally{
            Log.info("finished");
        }
    }

    protected int handleRequest(int command) throws ProcessException{
        Log.info("handling command: " + command);
        lastCommand.set(command);
        analogValue.set(Math.PI * command);
        comment.set("last command handled '" + command + "'");
        busyTime.await();
        busyTime.await();
        return result++;
    }

    @Override
    protected void preCheckInterlocks() throws InputInterlockException {
    }

    @Override
    protected void postCheckInterlocks() throws OutputInterlockException {
    }

    @Override
    protected void inEveryCycleDo() throws ProcessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
