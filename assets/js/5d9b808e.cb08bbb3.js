(self.webpackChunkcomet_docs=self.webpackChunkcomet_docs||[]).push([[7840],{3905:function(t,e,r){"use strict";r.d(e,{Zo:function(){return u},kt:function(){return c}});var n=r(7294);function a(t,e,r){return e in t?Object.defineProperty(t,e,{value:r,enumerable:!0,configurable:!0,writable:!0}):t[e]=r,t}function i(t,e){var r=Object.keys(t);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(t);e&&(n=n.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),r.push.apply(r,n)}return r}function l(t){for(var e=1;e<arguments.length;e++){var r=null!=arguments[e]?arguments[e]:{};e%2?i(Object(r),!0).forEach((function(e){a(t,e,r[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(r,e))}))}return t}function o(t,e){if(null==t)return{};var r,n,a=function(t,e){if(null==t)return{};var r,n,a={},i=Object.keys(t);for(n=0;n<i.length;n++)r=i[n],e.indexOf(r)>=0||(a[r]=t[r]);return a}(t,e);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(t);for(n=0;n<i.length;n++)r=i[n],e.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(t,r)&&(a[r]=t[r])}return a}var p=n.createContext({}),d=function(t){var e=n.useContext(p),r=e;return t&&(r="function"==typeof t?t(e):l(l({},e),t)),r},u=function(t){var e=d(t.components);return n.createElement(p.Provider,{value:e},t.children)},m={inlineCode:"code",wrapper:function(t){var e=t.children;return n.createElement(n.Fragment,{},e)}},s=n.forwardRef((function(t,e){var r=t.components,a=t.mdxType,i=t.originalType,p=t.parentName,u=o(t,["components","mdxType","originalType","parentName"]),s=d(r),c=a,k=s["".concat(p,".").concat(c)]||s[c]||m[c]||i;return r?n.createElement(k,l(l({ref:e},u),{},{components:r})):n.createElement(k,l({ref:e},u))}));function c(t,e){var r=arguments,a=e&&e.mdxType;if("string"==typeof t||a){var i=r.length,l=new Array(i);l[0]=s;var o={};for(var p in e)hasOwnProperty.call(e,p)&&(o[p]=e[p]);o.originalType=t,o.mdxType="string"==typeof t?t:a,l[1]=o;for(var d=2;d<i;d++)l[d]=r[d];return n.createElement.apply(null,l)}return n.createElement.apply(null,r)}s.displayName="MDXCreateElement"},5863:function(t,e,r){"use strict";r.r(e),r.d(e,{frontMatter:function(){return l},metadata:function(){return o},toc:function(){return p},default:function(){return u}});var n=r(2122),a=r(9756),i=(r(7294),r(3905)),l={sidebar_position:20,title:"bqload"},o={unversionedId:"cli/bqload",id:"cli/bqload",isDocsHomePage:!1,title:"bqload",description:"Synopsis",source:"@site/docs/cli/bqload.md",sourceDirName:"cli",slug:"/cli/bqload",permalink:"/comet-data-pipeline/docs/cli/bqload",editUrl:"https://github.com/ebiznext/comet-data-pipeline/edit/master/docs/docs/cli/bqload.md",version:"current",sidebarPosition:20,frontMatter:{sidebar_position:20,title:"bqload"},sidebar:"cometSidebar",previous:{title:"import",permalink:"/comet-data-pipeline/docs/cli/import"},next:{title:"esload | index",permalink:"/comet-data-pipeline/docs/cli/esload"}},p=[{value:"Synopsis",id:"synopsis",children:[]},{value:"Description",id:"description",children:[]},{value:"Parameters",id:"parameters",children:[]}],d={toc:p};function u(t){var e=t.components,r=(0,a.Z)(t,["components"]);return(0,i.kt)("wrapper",(0,n.Z)({},d,r,{components:e,mdxType:"MDXLayout"}),(0,i.kt)("h2",{id:"synopsis"},"Synopsis"),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"comet bqload ","[options]")),(0,i.kt)("h2",{id:"description"},"Description"),(0,i.kt)("h2",{id:"parameters"},"Parameters"),(0,i.kt)("table",null,(0,i.kt)("thead",{parentName:"table"},(0,i.kt)("tr",{parentName:"thead"},(0,i.kt)("th",{parentName:"tr",align:null},"Parameter"),(0,i.kt)("th",{parentName:"tr",align:null},"Cardinality"),(0,i.kt)("th",{parentName:"tr",align:null},"Description"))),(0,i.kt)("tbody",{parentName:"table"},(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--source_file:",(0,i.kt)("inlineCode",{parentName:"td"},"<value>")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Required")),(0,i.kt)("td",{parentName:"tr",align:null},"Full Path to source file")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--output_dataset:",(0,i.kt)("inlineCode",{parentName:"td"},"<value>")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Required")),(0,i.kt)("td",{parentName:"tr",align:null},"BigQuery Output Dataset")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--output_table:",(0,i.kt)("inlineCode",{parentName:"td"},"<value>")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Required")),(0,i.kt)("td",{parentName:"tr",align:null},"BigQuery Output Table")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--output_partition:",(0,i.kt)("inlineCode",{parentName:"td"},"<value>")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Optional")),(0,i.kt)("td",{parentName:"tr",align:null},"BigQuery Partition Field")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--require_partition_filter:",(0,i.kt)("inlineCode",{parentName:"td"},"<value>")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Optional")),(0,i.kt)("td",{parentName:"tr",align:null},"Require Partition Filter")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--output_clustering:",(0,i.kt)("inlineCode",{parentName:"td"},"col1,col2...")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Optional")),(0,i.kt)("td",{parentName:"tr",align:null},"BigQuery Clustering Fields")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--options:",(0,i.kt)("inlineCode",{parentName:"td"},"k1=v1,k2=v2...")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Optional")),(0,i.kt)("td",{parentName:"tr",align:null},"BigQuery Sink Options")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--source_format:",(0,i.kt)("inlineCode",{parentName:"td"},"<value>")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Optional")),(0,i.kt)("td",{parentName:"tr",align:null},"Source Format eq. parquet. This option is ignored, Only parquet source format is supported at this time")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--create_disposition:",(0,i.kt)("inlineCode",{parentName:"td"},"<value>")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Optional")),(0,i.kt)("td",{parentName:"tr",align:null},"Big Query Create disposition ",(0,i.kt)("a",{parentName:"td",href:"https://cloud.google.com/bigquery/docs/reference/auditlogs/rest/Shared.Types/CreateDisposition"},"https://cloud.google.com/bigquery/docs/reference/auditlogs/rest/Shared.Types/CreateDisposition"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--write_disposition:",(0,i.kt)("inlineCode",{parentName:"td"},"<value>")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Optional")),(0,i.kt)("td",{parentName:"tr",align:null},"Big Query Write disposition ",(0,i.kt)("a",{parentName:"td",href:"https://cloud.google.com/bigquery/docs/reference/auditlogs/rest/Shared.Types/WriteDisposition"},"https://cloud.google.com/bigquery/docs/reference/auditlogs/rest/Shared.Types/WriteDisposition"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"--row_level_security:",(0,i.kt)("inlineCode",{parentName:"td"},"<value>")),(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("em",{parentName:"td"},"Optional")),(0,i.kt)("td",{parentName:"tr",align:null},"value is in the form name,filter,sa:",(0,i.kt)("a",{parentName:"td",href:"mailto:sa@mail.com"},"sa@mail.com"),",user:",(0,i.kt)("a",{parentName:"td",href:"mailto:user@mail.com"},"user@mail.com"),",group:",(0,i.kt)("a",{parentName:"td",href:"mailto:group@mail.com"},"group@mail.com"))))))}u.isMDXComponent=!0}}]);