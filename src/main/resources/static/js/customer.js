
async function fillFavouritesGoods() {
    let response = await fetch("/customer/favouritesGoods");
    let content = await response.json();
    let favoriteGoodsJson = document.getElementById('favouritesGoodsList');
    let key
    $(favoriteGoodsJson).empty();
    for (key in content) {
        let product = `
        <tr class=${content[key].id} id=${content[key].id}>
    <td>${content[key].id}</td>
    <td><input type="checkbox" name="${content[key].id}"/></td> 
    <td>${content[key].product}</td>
    <td>${content[key].price}</td>
    <td>${content[key].amount}</td>
    <td>
       <button class="btn btn-danger" onclick="deleteProductFromFavouritGoods(${content[key].id})">Удалить</button>
    </td>
    <td>
       <button class="btn btn-primary" onclick="addProductToBasket(${content[key].id})">Добавить в корзину</button>
    </td>
    <tr>
`;
        $(favoriteGoodsJson).append(product);
    }
}

async function deleteProductFromFavouritGoods(id) {
    await fetch("/customer/favouritesGoods", {
        method: "DELETE",
        body: id,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    await fillFavouritesGoods();
}

async function addProductToBasket(id) {
    await fetch(`/api/basket/add/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    fillBusket();
}

$(document).on("click", "#showBasket", function () {
    $('#v-pills-tab a[href="#basketGoods"]').tab('show')
});

$(document).on("click", "#add-group-buton", function () {
    // let nameGroup = prompt("Введите название группы \"Избранных товаров\" ");
    // if (nameGroup) {
    //     $('#favouritesGroup').append("<option value='All'>" + nameGroup + " </option>");
    //     addFavouritesGroupInBD(nameGroup);
    // }
    addFavouritesGroupInBD();
    //$('#v-pills-tab a[href="#basketGoods"]').tab('show')
});

function addFavouritesGroupInBD() {
    let result = fetch('/customer/favouritesGroup').then( result => {
        return result.json();
    };
    console.log(result);
};