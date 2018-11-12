/*
 * generated by Xtext 2.12.0
 */
package tablesaw.xtext.tests

import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import tablesaw.xtext.xaw.Xaw

@RunWith(XtextRunner)
@InjectWith(XawInjectorProvider)
class XawParsingTest {
	
	@Inject extension ValidationTestHelper

	@Inject extension ParseHelper<Xaw>	
	
	@Test
	def void loadModel() {
		parse('''
			xaw tablesaw.xtext.tests.LoadModelTest
			val halAge = 52
			val table1 =
			# String name, short age #
			| "Hallvard", halAge |
			def String helper1(String s) s
		''').assertNoErrors
	}
}
