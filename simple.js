/*!
 * node-opmlparser
 * Copyright(c) 2011-2014 Dan MacTough <danmactough@gmail.com>
 * MIT Licensed
 */

var OpmlParser = require('opmlparser')
  , request = require('request')
  , fs = require('fs');

var opmlparser = new OpmlParser()
  , counter = 0;

/* remote opml */
/*
var req = request('http://hosting.opml.org/dave/spec/subscriptionList.opml');

req.on('error', done);
req.on('response', function (res) {
  if (res.statusCode != 200) return done(new Error('Bad status code'));
  this.pipe(opmlparser);
})
*/

/* local opml */
var opmlfile = '/home/shiva/code/samiska.git/samiska/etc/feedly.opml';
var source = fs.createReadStream(opmlfile);
source.pipe(opmlparser);

opmlparser.on('error', done);
opmlparser.once('readable', function () {
  console.log('This OPML is entitled: "%s"', this.meta.title);
});

opmlparser.on('readable', function() {
  var outline;

  while (outline = this.read()) {
    if (outline['#type'] === 'feed') {
      counter++;
      console.log('Got feed: "%s" <%s>', outline.title, outline.xmlurl);
    }
  }
});

opmlparser.on('end', function () {
  console.log('All done. Found %s feeds.', counter);
});

function done (err) {
  if (err) {
    console.log(err, err.stack);
    return process.exit(1);
  }
  process.exit();
}
