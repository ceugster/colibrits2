/*
 * Created on 2009 2 8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.report.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ApplicationInstanceManager
{
	private static ApplicationInstanceListener subListener;

	public static final int SINGLE_INSTANCE_NETWORK_SOCKET = 44332;

	public static final String SINGLE_INSTANCE_SHARED_KEY = "$$NewInstance$$\n";

	private static String pluginId;

	public static boolean registerInstance(final String pluginId)
	{
		if (ApplicationInstanceManager.pluginId == null)
		{
			ApplicationInstanceManager.pluginId = pluginId;
		}
		else
		{
			if (ApplicationInstanceManager.pluginId.equals(pluginId))
			{
				return true;
			}
		}

		final boolean returnValueOnError = true;

		try
		{
			final ServerSocket socket = new ServerSocket(ApplicationInstanceManager.SINGLE_INSTANCE_NETWORK_SOCKET, 10,
					InetAddress.getLocalHost());

			final Thread instanceListenerThread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					boolean socketClosed = false;
					while (!socketClosed)
					{
						if (socket.isClosed())
						{
							socketClosed = true;
						}
						else
						{
							try
							{
								final Socket client = socket.accept();
								final BufferedReader in = new BufferedReader(new InputStreamReader(client
										.getInputStream()));
								final String message = in.readLine();
								if (ApplicationInstanceManager.SINGLE_INSTANCE_SHARED_KEY.trim().equals(message.trim()))
								{
									if (ApplicationInstanceManager.subListener != null)
									{
										ApplicationInstanceManager.subListener.newInstanceCreated();
									}
								}
								in.close();
								client.close();
							}
							catch (final IOException e)
							{
								socketClosed = true;
							}
						}
					}
				}
			});
			instanceListenerThread.start();
		}
		catch (final UnknownHostException e)
		{
			return returnValueOnError;
		}
		catch (final IOException e)
		{
			try
			{
				final Socket clientSocket = new Socket(InetAddress.getLocalHost(),
						ApplicationInstanceManager.SINGLE_INSTANCE_NETWORK_SOCKET);
				final OutputStream out = clientSocket.getOutputStream();
				out.write(ApplicationInstanceManager.SINGLE_INSTANCE_SHARED_KEY.getBytes());
				out.close();
				clientSocket.close();
				return false;
			}
			catch (final UnknownHostException e2)
			{
				return returnValueOnError;
			}
			catch (final IOException e2)
			{
				return returnValueOnError;
			}
		}
		return true;
	}

	public static void setApplicationInstanceListener(final ApplicationInstanceListener listener)
	{
		ApplicationInstanceManager.subListener = listener;
	}
}
