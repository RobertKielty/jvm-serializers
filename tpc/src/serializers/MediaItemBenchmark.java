package serializers;

import java.io.PrintWriter;

/**
 * Intermediate base class for tests that use a single <code>MediaItem</code>
 * as data.
 * The only method missing is <code>addTests</code>, which defines codecs to use.
 */
public abstract class MediaItemBenchmark extends BenchmarkBase
{
    public MediaItemBenchmark() { }
    
    protected void runBenchmark(String[] args) {
        runBenchmark(args,
                Create,
                Serialize,
                Deserialize);
    }
    
    @Override
    protected <J> byte[] serializeForSize(Transformer<J,Object> transformer, Serializer<Object> serializer, J value)
        throws Exception
    {
        return serializer.serialize(transformer.forward(value));
    }
    
    @Override
    protected <J> void checkCorrectness(PrintWriter errors, Transformer<J,Object> transformer,
            Serializer<Object> serializer, J value)
        throws Exception
    {
        checkSingleItem(errors, transformer, serializer, value);
    }
    
    @Override
    protected Object convertTestData(TestGroup.Entry<?,Object> loader, Params params, byte[] data)
            throws Exception
    {
        Object deserialized = loader.serializer.deserialize(data);
        return loader.transformer.reverse(deserialized);
    }

    // ------------------------------------------------------------------------------------
    // Test case objects
    // ------------------------------------------------------------------------------------
    
    protected final TestCase Create = new TestCase()
    {
            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
            {
                    long start = System.nanoTime();
                    for (int i = 0; i < iterations; i++)
                    {
                            transformer.forward(value);
                    }
                    return iterationTime(System.nanoTime() - start, iterations);
            }
    };

    protected final TestCase Serialize = new TestCase()
    {
            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
            {
                    Object[] objects = new Object[iterations];
                    for (int i = 0; i < iterations; i++)
                    {
                  	  objects[i] = transformer.forward(value);
                    }
                    long start = System.nanoTime();
                    for (int i = 0; i < iterations; i++)
                    {
                  	  objects[i] = serializer.serialize(objects[i]);
                    }
                    return iterationTime(System.nanoTime() - start, iterations);
            }
    };

    protected final TestCase Deserialize = new TestCase()
    {
            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
            {
                    byte[] array = serializer.serialize(transformer.forward(value));
                    long start = System.nanoTime();
                    for (int i = 0; i < iterations; i++)
                    {
                            serializer.deserialize(array);
                    }
                    return iterationTime(System.nanoTime() - start, iterations);
            }
    };

// 16-May-2012, Nate: As discussed on mailing list, removed these two as they only exist in an attempt to
// make the comparison with ActiveMQProtobuf fair with the rest of the serializers. This adds overhead to
// the rest of the serializers, skewing the results. ActiveMQProtobuf has been disabled, so these aren't needed.
//    protected final TestCase DeserializeAndCheck = new TestCase()
//    {
//            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
//            {
//                    byte[] array = serializer.serialize(transformer.forward(value));
//                    long start = System.nanoTime();
//                    for (int i = 0; i < iterations; i++)
//                    {
//                            Object obj = serializer.deserialize(array);
//                            transformer.reverse(obj);
//                    }
//                    return iterationTime(System.nanoTime() - start, iterations);
//            }
//    };
//
//    protected final TestCase DeserializeAndCheckShallow = new TestCase()
//    {
//            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
//            {
//                    byte[] array = serializer.serialize(transformer.forward(value));
//                    long start = System.nanoTime();
//                    for (int i = 0; i < iterations; i++)
//                    {
//                            Object obj = serializer.deserialize(array);
//                            transformer.shallowReverse(obj);
//                    }
//                    return iterationTime(System.nanoTime() - start, iterations);
//            }
//    };    
}
