SpaceCore – Java LWJGL Flight Simulator
=========

April 17, 2012 by Jeremy Bridon (cores2.com)

I wrote a little space-flight over this past weekend. I wanted to learn more about Java, LWJGL (an OpenGL java bindings library), and most importantly: how a slow processor-side run-time affect graphical performance.

I’ve mentioned in a previous post that Java, and other byte-code languages, actually use a JIT (Just-in-Time) compilation technique to really speed up execution. Still, even with such technologies, the run-times are not as fast as native-languages (i.e. compiled into native code, like C or C++), yet what I learned was that it doesn’t (or shouldn’t, if you know what you’re doing) matter!

Game development is complex, and really requires a big effort to distinguish between processor-side computing and graphics-side computing. In the end, even if Java wasn’t JIT-based, a simple video game shouldn’t require heavy processor-side computing, as much of your work really should be pushed onto the graphics card. Though my game is tiny and requires little processing, I can see why people would used managed languages & environments for programming games: the tools and easier, you can develop faster, and all of the built-in libraries really help enormously!

Video: http://www.youtube.com/watch?v=XhFcmbo4abw

Above is the video game I worked on. There is no story or premise: again, the whole point was to see if I could prototype a game very quickly. Not mentioned in the video was that I had to re-learn quaternions and use that to help with ship control and rotations, rather than directly use Euler-based pitch/yaw.

Interested in reading the source, or executing the game yourself? Grab it here! Be warned: I butchered the source code for the sake of experimentation, and this is very far from any professional code I would write on a real-world project.