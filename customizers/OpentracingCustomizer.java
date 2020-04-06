package customziers;

// camel-k: language=java

import org.apache.camel.BindToRegistry;
import org.apache.camel.CamelContext;
import org.apache.camel.PropertyInject;
import org.apache.camel.opentracing.OpenTracingTracer;

import io.opentracing.Tracer;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;

public class OpentracingCustomizer {

    @BindToRegistry
    public static OpenTracingTracer tracer(
        CamelContext ctx, 
        @PropertyInject("env:CAMEL_K_INTEGRATION") String name, 
        @PropertyInject("jaeger.endpoint") String endpoint) {

            OpenTracingTracer openTracingTracer = new OpenTracingTracer();
            openTracingTracer.setTracer(new Configuration(name)
                .withReporter(new ReporterConfiguration()
                    .withSender(new SenderConfiguration()
                        .withEndpoint(endpoint)
                    )
                )
                .withSampler(new SamplerConfiguration()
                    .withType("const")    
                    .withParam(1)
                )
                .getTracer()
            );
            openTracingTracer.init(ctx);
            return openTracingTracer;
    }

}