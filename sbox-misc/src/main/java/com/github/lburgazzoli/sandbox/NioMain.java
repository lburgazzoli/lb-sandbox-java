package com.github.lburgazzoli.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class NioMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(NioMain.class);

    // *************************************************************************
    //
    // *************************************************************************

    private static final class Server implements Runnable {
        public void run() {
            try {
                ServerSocketChannel server = ServerSocketChannel.open();
                server.socket().bind(new InetSocketAddress(9999));

                SocketChannel client = server.accept();
                client.configureBlocking(false);
                client.socket().setSoLinger(false, 0);
                client.socket().setTcpNoDelay(true);
                client.socket().setSoTimeout(0);

                Selector sel = Selector.open();
                client.register(sel, SelectionKey.OP_READ);

                ByteBuffer buf = ByteBuffer.allocateDirect(1024);

                for(int i=0; ; i++) {
                    if(sel.select() > 0) {
                        final Set<SelectionKey> keys = sel.selectedKeys();
                        for(final SelectionKey key : keys) {
                            if(key.isReadable()) {
                                buf.clear();
                                buf.limit(16);
                                while(buf.remaining() > 0) {
                                    if(client.read(buf) < 0) {
                                        LOGGER.warn("EOF");
                                        return;
                                    }
                                }

                                buf.flip();

                                LOGGER.info("{} - long1={}, log2={}",
                                    i,
                                    buf.getLong(),
                                    buf.getLong()
                                );
                            }
                        }

                        keys.clear();
                    }
                }

            } catch (Exception e) {
                LOGGER.warn("", e);
            }
        }
    }

    private static final class Client implements Runnable {
        public void run() {
            try {
                SocketChannel client = SocketChannel.open();
                client.socket().connect(new InetSocketAddress(9999));
                client.configureBlocking(true);
                client.socket().setSoLinger(false, 0);
                client.socket().setTcpNoDelay(true);
                client.socket().setSoTimeout(0);

                ByteBuffer buf = ByteBuffer.allocateDirect(1024);

                buf.putLong(1L);
                buf.putLong(1L);
                buf.putLong(1L);
                buf.putLong(2L);
                buf.putLong(1L);
                buf.putLong(3L);

                buf.flip();
                while(buf.remaining() > 0) {
                    client.write(buf);
                }

                Thread.sleep(1000 * 60 * 5);
            } catch (Exception e) {
                LOGGER.warn("", e);
            }
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    public static void main(String[] args) throws Exception {
        Thread srv = new Thread(new Server());
        srv.start();

        Thread.sleep(1000 * 5);

        Thread cli = new Thread(new Client());
        cli.start();

        srv.join();
        cli.join();
    }
}
