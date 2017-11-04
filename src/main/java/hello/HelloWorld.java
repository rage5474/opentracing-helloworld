package hello;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import com.uber.jaeger.Configuration;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format.Builtin;
import io.opentracing.propagation.TextMapInjectAdapter;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

public class HelloWorld {
	public static void main(String[] args) throws InterruptedException, IOException {

		Tracer tracer = new Configuration("HelloWorldClient", new Configuration.SamplerConfiguration("const", 1),
				new Configuration.ReporterConfiguration(true, "localhost", 5775, 1000, 10000)).getTracer();

		GlobalTracer.register(tracer);

		try (ActiveSpan activeSpan = tracer.buildSpan("send").withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
				.withTag(Tags.COMPONENT.getKey(), "HelloWorldClient").startActive()) {

			sendClientParametersToServer(tracer, activeSpan);
			System.out.println("Hello World");
		}
		// Important because reporter is only running each second
		Thread.sleep(2000);
	}

	private static void sendClientParametersToServer(Tracer tracer, ActiveSpan activeSpan)
			throws UnknownHostException, IOException {
		Socket socketClient = new Socket("localhost", 5555);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
		HashMap<String, String> map = new HashMap<String, String>();
		TextMapInjectAdapter carrier = new TextMapInjectAdapter(map);
		tracer.inject(activeSpan.context(), Builtin.TEXT_MAP, carrier);

		writer.write(map.get("uber-trace-id"));
		writer.newLine();
		writer.flush();
		writer.close();
		socketClient.close();
	}
}
