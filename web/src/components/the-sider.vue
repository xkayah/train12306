<template>
    <a-layout-sider v-model:collapsed="collapsed" collapsible>
        <div class="logo"/>
        <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="inline">
            <a-menu-item key="1">
                <pie-chart-outlined/>
                <span>Option 1</span>
            </a-menu-item>
            <a-menu-item key="2">
                <desktop-outlined/>
                <span>Option 2</span>
            </a-menu-item>
            <a-sub-menu key="sub1">
                <template #title>
            <span>
              <user-outlined/>
              <span>User</span>
            </span>
                </template>
                <a-menu-item key="3">Tom</a-menu-item>
                <a-menu-item key="4">Bill</a-menu-item>
                <a-menu-item key="5">Alex</a-menu-item>
            </a-sub-menu>
            <a-sub-menu key="sub2">
                <template #title>
            <span>
              <team-outlined/>
              <span>Team</span>
            </span>
                </template>
                <a-menu-item key="6">Team 1</a-menu-item>
                <a-menu-item key="8">Team 2</a-menu-item>
            </a-sub-menu>
            <a-menu-item key="9">
                <file-outlined/>
                <span>File</span>
            </a-menu-item>
        </a-menu>
    </a-layout-sider>
</template>

<script>
import {defineComponent} from 'vue';
import store from "@/store";

export default defineComponent({
    name: "the-sider",
    components: {},
    setup() {
        let user = store.state.user;
        return {
            user,
        };
    },
});
</script>

<style scoped>

</style>
