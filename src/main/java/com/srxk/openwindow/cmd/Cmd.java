package com.srxk.openwindow.cmd;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liulaoye on 17-2-28.
 * CMD ENUM
 */
public enum Cmd {
    LOGIN_CMD( 2 ),
    RAINFALL_CMD( 1 ), UNDEFINED_CMD( 0 ),CTRL_CMD(3), OPEN_WINDOW(5), CLOSE_WINDOW(6), STOP_WINDOW(
        7), QUERY_WINDOW(8);

    private final int number;

    private static final Map<Integer, Cmd> numToEnum = new HashMap<Integer, Cmd>();

    static{
        for( Cmd t : values() ) {

            Cmd s = numToEnum.put( t.number, t );
            if( s != null ) {
                throw new RuntimeException( t.number + "重复了" );
            }
        }
    }

    Cmd( int number ){
        this.number = number;
    }

    public int toNum(){
        return number;
    }

    public static Cmd fromNum( int n ){
        final Cmd cmd = numToEnum.get( n );
        if( cmd == null){
            return Cmd.UNDEFINED_CMD;
        }
        return cmd;
    }


}
