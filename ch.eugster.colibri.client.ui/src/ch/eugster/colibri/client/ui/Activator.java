package ch.eugster.colibri.client.ui;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	private ServiceTracker<LogService, LogService> logServiceTracker;

	private LogService logService;
	
	private Frame frame;

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	public BufferedImage convertToAWT(final ImageData data)
	{
		ColorModel colorModel = null;
		final PaletteData palette = data.palette;
		if (palette.isDirect)
		{
			colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
			final BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height),
					false, null);
			final WritableRaster raster = bufferedImage.getRaster();
			final int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					final int pixel = data.getPixel(x, y);
					final RGB rgb = palette.getRGB(pixel);
					pixelArray[0] = rgb.red;
					pixelArray[1] = rgb.green;
					pixelArray[2] = rgb.blue;
					raster.setPixels(x, y, 1, 1, pixelArray);
				}
			}
			return bufferedImage;
		}
		else
		{
			final RGB[] rgbs = palette.getRGBs();
			final byte[] red = new byte[rgbs.length];
			final byte[] green = new byte[rgbs.length];
			final byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++)
			{
				final RGB rgb = rgbs[i];
				red[i] = (byte) rgb.red;
				green[i] = (byte) rgb.green;
				blue[i] = (byte) rgb.blue;
			}
			if (data.transparentPixel != -1)
			{
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
			}
			else
			{
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
			}
			final BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height),
					false, null);
			final WritableRaster raster = bufferedImage.getRaster();
			final int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					final int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}

	public ImageData convertToSWT(final BufferedImage bufferedImage)
	{
		if (bufferedImage.getColorModel() instanceof DirectColorModel)
		{
			final DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
			final PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			final ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			final WritableRaster raster = bufferedImage.getRaster();
			final int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					raster.getPixel(x, y, pixelArray);
					final int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		}
		else if (bufferedImage.getColorModel() instanceof IndexColorModel)
		{
			final IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
			final int size = colorModel.getMapSize();
			final byte[] reds = new byte[size];
			final byte[] greens = new byte[size];
			final byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			final RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++)
			{
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			final PaletteData palette = new PaletteData(rgbs);
			final ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			final WritableRaster raster = bufferedImage.getRaster();
			final int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}

	public Frame getFrame()
	{
		return this.frame;
	}

	@Override
	public void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put("function.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/function.png")));
		imageRegistry.put("login.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/login.png")));
		imageRegistry.put("wait.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/wait.gif")));
		imageRegistry.put("nowait.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/nowait.gif")));
		imageRegistry.put("generic_element.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/generic_element.gif")));
		imageRegistry.put("generic_element_yellow.gif",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/generic_element_yellow.gif")));
		imageRegistry.put("generic_elements.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/generic_elements.gif")));
		imageRegistry.put("sunshine.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/sunshine.png")));
		imageRegistry.put("ok", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/ok_16.png")));
		imageRegistry.put("exclamation", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/exclamation_16.png")));
		imageRegistry.put("error", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/error_16.png")));
		imageRegistry.put("question", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/question_16.png")));

		imageRegistry.put("metal-error.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/metal-error.gif")));
		imageRegistry.put("metal-inform.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/metal-inform.gif")));
		imageRegistry.put("metal-question.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/metal-question.gif")));
		imageRegistry.put("metal-warn.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/metal-warn.gif")));
	}

	public void setFrame(final Frame frame)
	{
		this.frame = frame;
	}

	public void log(int level, String message)
	{
		if (this.logServiceTracker != null)
		{
			this.logService.log(level, message);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception
	{
		super.start(context);
		Activator.plugin = this;

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();

		logService = (LogService) this.logServiceTracker.getService();
		log(LogService.LOG_DEBUG, "Bundle " + context.getBundle().getSymbolicName() + " gestartet.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception
	{
		log(LogService.LOG_DEBUG, "Bundle " + context.getBundle().getSymbolicName() + " gestoppt.");
		this.logServiceTracker.close();
		Activator.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return Activator.plugin;
	}
}
