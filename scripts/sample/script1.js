var i = 0;

out.header(["Hello"]);

out.line(["args " + args]);
for (var i=0; i<10; i++) {
    out.line(["test", ""+i]);
}