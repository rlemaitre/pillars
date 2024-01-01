// utilities
const get = function (selector, scope) {
  scope = scope ? scope : document;
  return scope.querySelector(selector);
};

const getAll = function (selector, scope) {
  scope = scope ? scope : document;
  return scope.querySelectorAll(selector);
};

let i;
// setup typewriter effect in the terminal demo
if (document.getElementsByClassName('demo').length > 0) {
  let i = 0;
  const txt = `scribbler
            [Entry mode; press Ctrl+D to save and quit; press Ctrl+C to quit without saving]

            ###todo for new year dinner party

            - milk
            - butter
            - green onion
            - lots and lots of kiwis 🥝`;
  const speed = 60;

  function typeItOut () {
    if (i < txt.length) {
      document.getElementsByClassName('demo')[0].innerHTML += txt.charAt(i);
      i++;
      setTimeout(typeItOut, speed);
    }
  }

  setTimeout(typeItOut, 1800);
}

// toggle tabs on codeblock
window.addEventListener("load", function() {
  // get all tab_containers in the document
  const tabContainers = getAll(".tab__container");

  // bind click event to each tab container
  for (let i = 0; i < tabContainers.length; i++) {
    get('.tab__menu', tabContainers[i]).addEventListener("click", tabClick);
  }

  // each click event is scoped to the tab_container
  function tabClick (event) {
    const scope = event.currentTarget.parentNode;
    const clickedTab = event.target;
    const tabs = getAll('.tab', scope);
    const panes = getAll('.tab__pane', scope);
    const activePane = get(`.${clickedTab.getAttribute('data-tab')}`, scope);

    // remove all active tab classes
    for (let i = 0; i < tabs.length; i++) {
      tabs[i].classList.remove('active');
    }

    // remove all active pane classes
    for (let i = 0; i < panes.length; i++) {
      panes[i].classList.remove('active');
    }

    // apply active classes on desired tab and pane
    clickedTab.classList.add('active');
    activePane.classList.add('active');
  }
});

//in page scrolling for documentation page
let btns = getAll('.js-btn');
let sections = getAll('.js-section');

function setActiveLink(event) {
  // remove all active tab classes
  for (let i = 0; i < btns.length; i++) {
    btns[i].classList.remove('selected');
  }

  event.target.classList.add('selected');
}

function smoothScrollTo(i, event) {
  const element = sections[i];
  setActiveLink(event);

  window.scrollTo({
    'behavior': 'smooth',
    'top': element.offsetTop - 20,
    'left': 0
  });
}

if (btns.length && sections.length > 0) {
  for (i = 0; i<btns.length; i++) {
    btns[i].addEventListener('click', smoothScrollTo.bind(this,i));
  }
}

// fix menu to page-top once user starts scrolling
window.addEventListener('scroll', function () {
  const docNav = get('.doc__nav > ul');

  if( docNav) {
    if (window.pageYOffset > 63) {
      docNav.classList.add('fixed');
    } else {
      docNav.classList.remove('fixed');
    }
  }
});

// responsive navigation
const topNav = get('.menu');
const icon = get('.toggle');

window.addEventListener('load', function(){
  function showNav() {
    if (topNav.className === 'menu') {
      topNav.className += ' responsive';
      icon.className += ' open';
    } else {
      topNav.className = 'menu';
      icon.classList.remove('open');
    }
  }
  icon.addEventListener('click', showNav);
});

