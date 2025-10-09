package com.avereon.cartesia.tool;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignPrint;
import com.avereon.product.Rb;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.resource.Resource;
import com.avereon.xenon.notice.Notice;
import com.avereon.xenon.task.Task;
import lombok.CustomLog;
import lombok.Getter;

import javax.print.PrintService;
import java.awt.*;
import java.awt.print.*;
import java.util.Arrays;
import java.util.Optional;

@Getter
@CustomLog
public class DesignAwtPrintTask extends Task<Void> {

	private final Xenon program;

	private final BaseDesignTool tool;

	private final Resource resource;

	private final DesignPrint print;

	public DesignAwtPrintTask( final Xenon program, final BaseDesignTool tool, final Resource resource, final DesignPrint print ) {
		this.program = program;
		this.tool = tool;
		this.resource = resource;
		this.print = print;
		setName( Rb.textOr( RbKey.LABEL, "print", "Print" ) + " " + resource.getName() );
	}

	@Override
	public Void call() throws Exception {
		log.atDebug().log( "Starting design print task..." );

		Optional<PrintService> optionalPrintService = Arrays.stream( PrinterJob.lookupPrintServices() ).filter( s -> "PDF".equalsIgnoreCase( s.getName() ) ).findAny();
		if( optionalPrintService.isEmpty() ) {
			getProgram().getNoticeManager().addNotice( new Notice( "Missing Print Service", "Print service not found: PDF" ) );
			return null;
		}

		Paper paper = new Paper();
		paper.setSize( 20 * 72, 30 * 72 );

		PageFormat pageFormat = new PageFormat();
		pageFormat.setOrientation( PageFormat.PORTRAIT );
		pageFormat.setPaper( paper );

		Printable painter = new Printable() {

			@Override
			public int print( Graphics graphics, PageFormat pageFormat, int pageIndex ) throws PrinterException {
				// TODO This will need to be matched up with the design renderer
				return pageIndex  <1 ? Printable.PAGE_EXISTS : Printable.NO_SUCH_PAGE;
			}
		};

		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintService( optionalPrintService.get() );
		job.setPrintable( painter, pageFormat );

		log.atConfig().log( "Printing " + resource + "..." );
		job.print();
		if( job.printDialog() ) {
			job.print();
			log.atConfig().log( "Printed " + resource + "." );
		} else {
			log.atConfig().log( "Printed cancelled." );
		}

		return null;
	}

}
