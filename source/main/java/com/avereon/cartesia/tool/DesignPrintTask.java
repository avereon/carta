package com.avereon.cartesia.tool;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignPrint;
import com.avereon.product.Rb;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.notice.Notice;
import com.avereon.xenon.task.Task;
import com.avereon.zarra.color.Colors;
import com.avereon.zarra.color.Paints;
import javafx.print.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.CustomLog;
import lombok.Getter;

@Getter
@CustomLog
public class DesignPrintTask extends Task<Void> {

	private final Xenon program;

	private final BaseDesignTool tool;

	private final Asset asset;

	private final DesignPrint print;

	public DesignPrintTask( final Xenon program, final BaseDesignTool tool, final Asset asset, final DesignPrint print ) {
		this.program = program;
		this.tool = tool;
		this.asset = asset;
		this.print = print;
		setName( Rb.textOr( RbKey.LABEL, "print", "Print" ) + " " + asset.getName() );
	}

	@Override
	public Void call() throws Exception {
		log.atDebug().log( "Starting design print task..." );

		// NEXT Tactic is to try a bunch of small canvases contained in a GridPane

		//		boolean successful = job.printPage( layout, renderer ) && job.endJob();
		//		if( successful ) {
		//			getProgram().getNoticeManager().addNotice( new Notice( "Print Job Success", this.getName() ) );
		//		} else {
		//			getProgram().getNoticeManager().addNotice( new Notice( "Print Job Failure", this.getName() ) );
		//		}

		log.atInfo().log( "Design print task complete." );

		return null;
	}

	private Void printWithSingleRenderPane() throws Exception {
		log.atDebug().log( "Starting design print task..." );
		// TODO This should come from the DesignPrint setup
		// There is no concept of custom paper sizes in FX.
		// According to FX the authoritative size for paper is from the printer itself
		// However, the Paper class has common sizes that can be used for convenience.
		Paper paper = Paper.NA_LETTER;

		// TODO This should come from the DesignPrint setup
		PageOrientation orientation = PageOrientation.PORTRAIT;

		// TODO These should come from the DesignPrint setup
		double leftMargin = 0.5 * 72;
		double rightMargin = 0.5 * 72;
		double topMargin = 0.5 * 72;
		double bottomMargin = 0.5 * 72;

		Printer printer = Printer.getDefaultPrinter();
		for( Printer p : Printer.getAllPrinters() ) {
			if( p.getName().equalsIgnoreCase( "PDF" ) ) {
				printer = p;
				break;
			}
		}

		final PrinterJob job = PrinterJob.createPrinterJob();
		job.setPrinter( printer );
		//final Printer printer = job.getPrinter();
		final PageLayout layout = printer.createPageLayout( paper, orientation, leftMargin, rightMargin, topMargin, bottomMargin );
		printer.getPrinterAttributes().getSupportedPrintResolutions().forEach( r -> log.atDebug().log( "Supported resolution: " + r ) );
		log.atConfig().log( "Default resolution: " + printer.getPrinterAttributes().getDefaultPrintResolution() );

		PrintResolution highest = printer.getPrinterAttributes().getDefaultPrintResolution();
		for( PrintResolution resolution : printer.getPrinterAttributes().getSupportedPrintResolutions() ) {
			if( resolution.getFeedResolution() > highest.getFeedResolution() ) highest = resolution;
		}

		JobSettings settings = job.getJobSettings();
		settings.setPageLayout( layout );
		settings.setPaperSource( PaperSource.AUTOMATIC );
		settings.setPrintResolution( highest );
		settings.setPrintQuality( PrintQuality.HIGH );
		log.atConfig().log( "Job resolution: " + settings.getPrintResolution() );

		// NOTE This can be used to give feedback to the user. It can be bound to a text field
		job.jobStatusProperty().asString();

		// NOTE This is a rather Swing looking dialog, maybe handle print properties separately
		//				boolean print = job.showPageSetupDialog( getScene().getWindow() );
		//				boolean print = job.showPrintDialog( getScene().getWindow() );
		//				if( !print ) return;

		//		double factor = 4;
		//
		//		// The NEW way
		//		final DesignRenderer renderer = new DesignRenderer();
		//		//renderer.setBackground( Background.fill( Color.LIGHTGRAY ) );
		//		renderer.setDesign( asset.getModel() );
		//		renderer.setVisibleLayers( tool.getVisibleLayers() );
		//
		//		renderer.setDpi( factor * 72 );
		//		renderer.setPrefWidth( layout.getPrintableWidth() );
		//		renderer.setPrefHeight( layout.getPrintableHeight() );
		//		renderer.setViewpoint( tool.getViewpoint() );
		//		renderer.setRotate( tool.getViewRotate() );
		//		renderer.setZoom( tool.getZoom(), tool.getZoom() );
		//
		//		//renderer.setReferenceLayerVisible( false );
		//
		//		// FIXME This is changing the actual geometry colors...not just copying them to the print
		//		// Invert the colors if using a dark theme
		//		// TODO This should eventually be a user preference
		//		//		if( getProgram().getWorkspaceManager().getThemeMetadata().isDark() ) {
		//		//			Fx.run( () -> renderer.getVisibleShapes().forEach( s -> {
		//		//				s.setDrawPaint( invertLuminance( s.calcDrawPaint() ) );
		//		//				s.setFillPaint( invertLuminance( s.calcFillPaint() ) );
		//		//			} ) );
		//		//		}
		//
		//		// Do the actual rendering
		//		// It is NOT required to do this on the FX thread
		//		renderer.printRender( factor );
		//		//Fx.run( () -> renderer.printRender( factor ) );
		//		//Fx.waitFor( 10000 );

		// FIXME Can't seem to get the factor above 3.2, about 230 dpi
		//  ...and it inconsistently prints nothing
		// The goal is to match the DPI requested by the printer/user
		// 300 dpi is a factor of 4.166666666666667
		// 2024 Aug 23 - Been as high as 13 today
		// Well, I think this is the issue: https://bugs.openjdk.org/browse/JDK-8090178
		// Which eventually leads to this:  https://bugs.openjdk.org/browse/JDK-8090822

		double factor = 10; // 720 dpi

		GridPane grid = new GridPane();
		grid.setPrefWidth( factor * layout.getPrintableWidth() );
		grid.setPrefHeight( factor * layout.getPrintableHeight() );

		Canvas renderer = new Canvas();
		//		renderer.setScaleX( 1.0 / factor ); // 0.1
		//		renderer.setScaleY( 1.0 / factor ); // 0.1
		//
		//		double vFactor = 36;
		//
		//		// Changing the width and height also caused it to be moved
		//		renderer.setWidth( factor * layout.getPrintableWidth() ); // 1440
		//		renderer.setHeight( factor * 2*vFactor ); // 1440
		//		renderer.setLayoutX( -((factor - 1) * (0.5*layout.getPrintableWidth())) ); // -648
		//		renderer.setLayoutY( -((factor - 1) * vFactor) ); // -648

		//		renderer.setTranslateX( -((factor - 1) * 72) );
		//		renderer.setTranslateY( -((factor - 1) * 72) );
		//		renderer.getGraphicsContext2D().setFill( Color.BLACK );
		//		renderer.getGraphicsContext2D().fillRect( 0, 0, factor * layout.getPrintableWidth(), factor * layout.getPrintableHeight() );
		//		renderer.getGraphicsContext2D().setFill( Color.YELLOW );
		//		renderer.getGraphicsContext2D().fillOval( 0, 0, factor * layout.getPrintableWidth(), factor * layout.getPrintableHeight() );

		renderer.getGraphicsContext2D().setFill( Color.BLACK );
		renderer.getGraphicsContext2D().fillRect( 0, 0, factor * 144, factor * 144 );
		renderer.getGraphicsContext2D().beginPath();
		renderer.getGraphicsContext2D().moveTo( 0, 0 );
		renderer.getGraphicsContext2D().lineTo( factor * 72, 0 );
		renderer.getGraphicsContext2D().lineTo( 0, factor * 72 );
		renderer.getGraphicsContext2D().closePath();
		renderer.getGraphicsContext2D().setFill( Color.YELLOW );
		renderer.getGraphicsContext2D().fill();
		log.atConfig().log( "Job size: " + layout.getPrintableWidth() + "x" + layout.getPrintableHeight() );
		log.atConfig().log( "Print size: " + renderer.getWidth() + "x" + renderer.getHeight() + " = " + (renderer.getWidth() * renderer.getHeight()) );

		boolean successful = job.printPage( layout, renderer ) && job.endJob();
		if( successful ) {
			getProgram().getNoticeManager().addNotice( new Notice( "Print Job Success", this.getName() ) );
		} else {
			getProgram().getNoticeManager().addNotice( new Notice( "Print Job Failure", this.getName() ) );
		}

		log.atInfo().log( "Design print task complete." );

		return null;
	}

	private String invertLuminance( Paint paint ) {
		if( paint instanceof Color color ) return Paints.toString( Colors.invertLuminance( color ) );
		return Paints.toString( paint );
	}

}
