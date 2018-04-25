package security.campus.api;

import java.io.IOException;

import org.owasp.html.Handler;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlStreamRenderer;
import org.owasp.html.PolicyFactory;

import com.google.common.base.Throwables;

public class HtmlContentSanitizer {

	public static final PolicyFactory POLICY_DEFINITION = new HtmlPolicyBuilder()
			.allowElements("p", "br").toFactory();
	public static final PolicyFactory STRICT_POLICY_DEFINITION = new HtmlPolicyBuilder()
	.allowElements().toFactory();

	public static String sanitizeHtml(String input) {
		StringBuilder builder = new StringBuilder();
		HtmlStreamRenderer renderer = HtmlStreamRenderer.create(builder,
		// Receives notifications on a failure to write to the output.
				new Handler<IOException>() {
					public void handle(IOException ex) {
						Throwables.propagate(ex); // System.out suppresses
													// IOExceptions
					}
				},
				// Our HTML parser is very lenient, but this receives
				// notifications on
				// truly bizarre inputs.
				new Handler<String>() {
					public void handle(String x) {
						throw new AssertionError(x);
					}
				});
		// Use the policy defined above to sanitize the HTML.
		HtmlSanitizer.sanitize(input, POLICY_DEFINITION.apply(renderer));
		return builder.toString();
	}
	
	public static String strictSanitizeHtml(String input) {
		StringBuilder builder = new StringBuilder();
		HtmlStreamRenderer renderer = HtmlStreamRenderer.create(builder,
				// Receives notifications on a failure to write to the output.
				new Handler<IOException>() {
			public void handle(IOException ex) {
				Throwables.propagate(ex); // System.out suppresses
				// IOExceptions
			}
		},
		// Our HTML parser is very lenient, but this receives
		// notifications on
		// truly bizarre inputs.
		new Handler<String>() {
			public void handle(String x) {
				throw new AssertionError(x);
			}
		});
		// Use the policy defined above to sanitize the HTML.
		HtmlSanitizer.sanitize(input, STRICT_POLICY_DEFINITION.apply(renderer));
		return builder.toString();
	}
}
