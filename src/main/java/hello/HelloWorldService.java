package hello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.uber.jaeger.Configuration;

import io.opentracing.ActiveSpan;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format.Builtin;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

public class HelloWorldService {

	private static Tracer tracer;

	public static void main(String[] args) throws InterruptedException, IOException {
		tracer = new Configuration("HelloWorldService", new Configuration.SamplerConfiguration("const", 1),
				new Configuration.ReporterConfiguration(true, "localhost", 5775, 1000, 10000)).getTracer();
		GlobalTracer.register(tracer);

		Map<String, String> clientParameters = waitForClientParameters();
		
		SpanContext context = tracer.extract(Builtin.TEXT_MAP, new TextMapExtractAdapter(clientParameters));
		try (ActiveSpan activeSpan = tracer.buildSpan("receive").withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)
				.withTag(Tags.COMPONENT.getKey(), "HelloWorldService").asChildOf(context).startActive()) {
			sayHello();
		}

		// Important because reporter is only running each second
		Thread.sleep(2000);
	}

	public static Map<String, String> waitForClientParameters() throws IOException{
		Map<String, String> map = new HashMap<String, String>();
		ServerSocket mysocket = new ServerSocket(5555);
		Socket connectionSocket = mysocket.accept();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		String line = reader.readLine();
		map.put("uber-trace-id", line);
		reader.close();
		mysocket.close();
		return map;
	}
	
	public static void sayHello() throws InterruptedException {
		Span child = tracer.buildSpan("ChildFunction").asChildOf(tracer.activeSpan()).startManual();
		child.log("sayHello");
		System.out.println("Hello World");
		Thread.sleep(800);
		child.finish();
	}
}
