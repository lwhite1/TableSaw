package tech.tablesaw.examples;

import java.util.Random;
import java.time.LocalDateTime;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;


import tech.tablesaw.plotly.traces.Trace;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Layout;  
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.event.HoverBroadcastBody;
import tech.tablesaw.plotly.event.HoverEventHandler;

public class HoverBroadcastExample {

    public static void main(String[] args) throws Exception {
    	  int seriesLen = 50;
    	  Random rng = new Random();
    	  Double[] prices = new Double[seriesLen];
    	  int[] volumes = new int[seriesLen];
    	  LocalDateTime startDT = LocalDateTime.of(2019, 1, 1, 9, 30, 0);
    	  LocalDateTime[] times = new LocalDateTime[seriesLen];
    	  

    	  HoverBroadcastBody hbb = HoverBroadcastBody.builder()
        .subPlots( new String[] {"xy", "xy2"} )
        .numTraces(2)
        .build();
        
    	  HoverEventHandler heh = HoverEventHandler.builder().body(hbb).build();
    	  
    	  for (int i = 0; i < seriesLen; i++) {
    	  	prices[i] = 25.0 + rng.nextDouble();
    	  	volumes[i] = rng.nextInt(10000);
    	  	times[i] = startDT.plusMinutes(i);
    	  }
    	  
    	  DateTimeColumn x = DateTimeColumn.create("time", times);
    	  DoubleColumn y2 = DoubleColumn.create("price", prices);
    	  IntColumn y1 = IntColumn.create("volume", volumes);
    	  
    	  ScatterTrace trace0 = ScatterTrace.builder( x, y2 )
        .showLegend(true)
        .name("Price")
        .mode(ScatterTrace.Mode.LINE)
        .yAxis(ScatterTrace.YAxis.Y2)
        .build();
        
        ScatterTrace trace1 = ScatterTrace.builder( x, y1 )
        .showLegend(true)
        .name("Volume")
        .mode(ScatterTrace.Mode.LINE)
        .yAxis(ScatterTrace.YAxis.Y)
        .build();
        
        Layout layout = Layout.builder("Stock Data", "Time", "Price")
        .yAxis( Axis.builder().title("volume").domain(0f, 0.25f).build() )
        .yAxis2( Axis.builder().title("price").domain(0.35f, 1.0f).build() )
        .build();
        Plot.show( new Figure(layout, heh, trace0, trace1) );
    	  
    }
}
