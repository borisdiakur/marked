package ut.com.borisdiakur.marked;

import org.junit.Test;
import com.borisdiakur.marked.MyPluginComponent;
import com.borisdiakur.marked.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}