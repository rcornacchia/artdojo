function upvoteButtonClicked(x) {
    var image = document.getElementById("upvoteButton");
    //var votes = 1337;
    if (image.src.match("http://cdn.mysitemyway.com/etc-mysitemyway/icons/legacy-previews/icons/glossy-silver-icons-arrows/008142-glossy-silver-icon-arrows-cut-arrow-up.png")) {
        image.src = "http://cdn.mysitemyway.com/etc-mysitemyway/icons/legacy-previews/icons/glossy-black-icons-arrows/007980-glossy-black-icon-arrows-arrow-thick-up.png";
        document.getElementById("numVotes").innerHTML++;
    } else {
        image.src = "http://cdn.mysitemyway.com/etc-mysitemyway/icons/legacy-previews/icons/glossy-silver-icons-arrows/008142-glossy-silver-icon-arrows-cut-arrow-up.png";
        document.getElementById("numVotes").innerHTML--;
    }
}
